package org.w01f.dds.layer2.sql.mysql.tree;

import java.util.List;

public class TableRecuNode extends TableRelNode {

	private TableRelNode tableRel;

	public TableRecuNode(TableRelNode tableRel) {
		this.tableRel = tableRel;
	}

	@Override
	public String toString() {
		return "(" + tableRel + ")";
	}

	public TableRelNode getTableRel() {
		return tableRel;
	}

	public void setTableRel(TableRelNode tableRel) {
		this.tableRel = tableRel;
	}

	@Override
	public List<TableRelNode.TableAndJoinMod> getRealTables() {
		return this.tableRel.getRealTables();
	}

}
