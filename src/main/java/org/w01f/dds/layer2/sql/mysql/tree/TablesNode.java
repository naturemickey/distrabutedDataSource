package org.w01f.dds.layer2.sql.mysql.tree;

import java.util.List;
import java.util.stream.Collectors;

public class TablesNode extends SQLSyntaxTreeNode {

	private List<TableRelNode> tableRels;

	public TablesNode(List<TableRelNode> tableRels) {
		this.tableRels = tableRels;
	}

	@Override
	public String toString() {
		return tableRels.stream().map(tr -> tr.toString()).collect(Collectors.joining(", "));
	}

	public List<TableRelNode.TableAndJoinMod> getRealTables() {
		return tableRels.stream().flatMap(tr -> tr.getRealTables().stream()).collect(Collectors.toList());
	}
}
