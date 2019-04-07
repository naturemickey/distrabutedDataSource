package org.w01f.dds.layer2.sql;

import org.w01f.dds.layer1.dsproxy.param.Param;
import org.w01f.dds.layer1.id.IDGenerator;
import org.w01f.dds.layer2.index.IndexConfigUtils;
import org.w01f.dds.layer2.index.config.Column;
import org.w01f.dds.layer2.index.config.Index;
import org.w01f.dds.layer2.sql.parser.mysql.tree.*;
import org.w01f.dds.layer2.sql.utils.SQLbreakUtil;
import org.w01f.dds.layer3.dataapi.IDataAccess;
import org.w01f.dds.layer3.indexapi.IIndexAccess;
import org.w01f.dds.layer4.data.DataAccess;
import org.w01f.dds.layer4.index.IndexAccess;
import org.w01f.dds.layer4.index.SQLBuildUtils;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class SqlHandler {

    private IIndexAccess indexAccess = new IndexAccess();
    private IDataAccess dataAccess = new DataAccess();

    public ResultSet executeQuery(StatNode statNode) {
        return null;
    }

    public int executeUpdate(StatNode statNode) {
        if (statNode.isInsert()) {
            InsertNode insertNode = statNode.getDmlAsInsert();
            Map<Index, Param[]> indexMap = SQLbreakUtil.parseInsertIndex(insertNode);

            this.indexAccess.insert(indexMap);

            final List<ElementPlaceholderNode> placeholderNodes = statNode.getPlaceholderNodes();
            final List<String> names = insertNode.getColumnNames().getNames();
            final ElementPlaceholderNode idHolder = placeholderNodes.get(names.indexOf("id"));
            final String id = idHolder.getParam().getValue()[1].toString();

            dataAccess.execute(statNode, IDGenerator.getDbNo(id));
        } else if (statNode.isUpdate()) {
            UpdateNode updateNode = statNode.getDmlAsUpdate();

            // TODO:
        } else if (statNode.isDelete()) {
            DeleteNode deleteNode = statNode.getDmlAsDelete();

            final String tableName = deleteNode.getTableNameAndAlias().getName();
            final List<List<ExpressionNode>> wheres = SQLbreakUtil.breakWhere(deleteNode.getWhereCondition());
            final List<Index> indices = IndexConfigUtils.getTableConfig(tableName).getIndices();

            for (List<ExpressionNode> andWhere : wheres) {
                final Index index = SQLbreakUtil.chooseIndex(andWhere, indices);

                if (index == null) {
                    // todo : maybe there are id
                    final List<String> ids = SQLbreakUtil.getIds(andWhere);
                    if (ids.isEmpty()) {
                        noIndexThrow(tableName, andWhere);
                    } else {
                        Set<Integer> dbNos = ids.stream().map(IDGenerator::getDbNo).collect(Collectors.toSet());

                        return dbNos.stream().mapToInt(dbNo -> this.dataAccess.executeUpdate(statNode, dbNo)).sum();
                    }
                } else {
                    final List<ExpressionNode> newDeleteWhereNodes = new ArrayList<>();
                    final List<ExpressionNode> newIndexWhereNodes = new ArrayList<>();

                    for (int i = 0; i < index.getColumns().length; i++) {
                        final Column column = index.getColumns()[i];

                        final ExpressionNode expression = getExpression(column.getName(), andWhere, i);
                        if (expression != null) {
                            newIndexWhereNodes.add(expression);
                        }
                    }

                    newDeleteWhereNodes.addAll(andWhere);

                    final StatNode selectIndexNode = SQLBuildUtils.sql4QueryIndex(index, newIndexWhereNodes);
                    ResultSet idRs = indexAccess.query(selectIndexNode);

                    // TODO
                }
            }
        }

        return 1;
    }

    private void noIndexThrow(String tableName, List<ExpressionNode> andWhere) {
        final StringBuilder sb = new StringBuilder("from ").append(tableName).append(" where");
        for (ExpressionNode expressionNode : andWhere) {
            sb.append(" ").append(expressionNode).append(" and ");
        }
        sb.delete(sb.length() - 5, sb.length());
        throw new RuntimeException("there is no index match: " + sb);
    }

    private ExpressionNode getExpression(String columnName, List<ExpressionNode> andWhere, int columnNo) {
        for (int i = 0; i < andWhere.size(); i++) {
            ExpressionNode expressionNode = andWhere.get(i);
            final ExpressionNode newExpressionNode = convertToNewWhere(columnName, expressionNode, columnNo);
            if (newExpressionNode != null) {
                andWhere.remove(i);

                return newExpressionNode;
            }
        }
        return null;
    }

    private ExpressionNode convertToNewWhere(String columnName, ExpressionNode expressionNode, int columnNo) {
        // only support these 4 types:
        if (expressionNode instanceof ExpressionRelationalNode) {
            final String op = ((ExpressionRelationalNode) expressionNode).getRelationalOp();
            final ElementNode left = ((ExpressionRelationalNode) expressionNode).getLeft();
            final ElementNode right = ((ExpressionRelationalNode) expressionNode).getRight();

            boolean isFirst = columnNo == 0;
            if ((isFirst && op.equals("=")) || !isFirst) {
                final ElementTextNode node = SQLbreakUtil.getElementTextNode(((ExpressionRelationalNode) expressionNode));
                if (node != null) {
                    if (SQLbreakUtil.textMatchColumn(columnName, node.getTxt())) {
                        final ElementTextNode elementTextNode = new ElementTextNode("v" + columnNo);
                        if (left == node) {
                            return new ExpressionRelationalNode(elementTextNode, right, op);
                        } else {
                            return new ExpressionRelationalNode(left, elementTextNode, op);
                        }
                    }
                }
            }
        } else if (expressionNode instanceof ExpressionBetweenAndNode) {
            final ElementNode element = ((ExpressionBetweenAndNode) expressionNode).getElement();
            final boolean not = ((ExpressionBetweenAndNode) expressionNode).isNot();
            final ElementNode left = ((ExpressionBetweenAndNode) expressionNode).getLeft();
            final ElementNode right = ((ExpressionBetweenAndNode) expressionNode).getRight();

            if (element instanceof ElementTextNode) {
                if (SQLbreakUtil.textMatchColumn(columnName, ((ElementTextNode) element).getTxt())) {
                    final ElementTextNode elementTextNode = new ElementTextNode("v" + columnNo);
                    return new ExpressionBetweenAndNode(elementTextNode, not, left, right);
                }
            }
        } else if (expressionNode instanceof ExpressionInValuesNode) {
            final ElementNode element = ((ExpressionInValuesNode) expressionNode).getElement();
            final boolean not = ((ExpressionInValuesNode) expressionNode).isNot();
            final ValueListNode values = ((ExpressionInValuesNode) expressionNode).getValues();

            if (element instanceof ElementTextNode) {
                if (SQLbreakUtil.textMatchColumn(columnName, ((ElementTextNode) element).getTxt())) {
                    final ElementTextNode elementTextNode = new ElementTextNode("v" + columnNo);
                    return new ExpressionInValuesNode(elementTextNode, not, values);
                }
            }
        } else if (expressionNode instanceof ExpressionLikeNode) {
            final ElementNode left = ((ExpressionLikeNode) expressionNode).getLeft();
            final boolean not = ((ExpressionLikeNode) expressionNode).isNot();
            final ElementNode right = ((ExpressionLikeNode) expressionNode).getRight();

            if (left instanceof ElementTextNode) {
                if (SQLbreakUtil.textMatchColumn(columnName, ((ElementTextNode) left).getTxt())) {
                    final ElementTextNode elementTextNode = new ElementTextNode("v" + columnNo);
                    return new ExpressionLikeNode(not, elementTextNode, right);
                }
            }
        }
        return null;
    }

    public boolean execute(StatNode statNode) {
        executeUpdate(statNode);
        return true;
    }
}
