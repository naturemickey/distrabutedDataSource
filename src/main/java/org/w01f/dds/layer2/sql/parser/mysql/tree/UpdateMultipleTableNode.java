package org.w01f.dds.layer2.sql.parser.mysql.tree;

public class UpdateMultipleTableNode extends UpdateNode  implements Cloneable {

	private final TableNameAndAliasesNode tableNameAndAliases;
	private final SetExprsNode setExprs;
	private final WhereConditionNode whereCondition;

	@Override
	public UpdateMultipleTableNode clone() {
		TableNameAndAliasesNode tableNameAndAliasesNode = tableNameAndAliases == null ? null :tableNameAndAliases.clone();
		SetExprsNode setExprsNode = setExprs == null ? null : setExprs.clone();
		WhereConditionNode whereConditionNode = whereCondition == null ? null : whereCondition.clone();
		return new UpdateMultipleTableNode(tableNameAndAliasesNode, setExprsNode, whereConditionNode);
	}

	public UpdateMultipleTableNode(TableNameAndAliasesNode tableNameAndAliases, SetExprsNode setExprs, WhereConditionNode whereCondition) {
		this.tableNameAndAliases = tableNameAndAliases;
		this.setExprs = setExprs;
		this.whereCondition = whereCondition;

		setParent(tableNameAndAliases, setExprs, whereCondition);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("update ").append(this.tableNameAndAliases).append(" set ").append(this.setExprs.toString());
		if (this.whereCondition != null) {
			sb.append(" where ").append(this.whereCondition.toString());
		}

		return sb.toString();
	}

	public TableNameAndAliasesNode getTableNameAndAliases() {
		return tableNameAndAliases;
	}

	public SetExprsNode getSetExprs() {
		return setExprs;
	}

	public WhereConditionNode getWhereCondition() {
		return whereCondition;
	}
}
