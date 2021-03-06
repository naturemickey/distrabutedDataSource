package org.w01f.dds.layer2.sql.parser.mysql.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ColumnNamesNode extends SQLSyntaxTreeNode  implements Cloneable {

	private final List<String> names;

	@Override
	public ColumnNamesNode clone() {
		return this;
	}

	public ColumnNamesNode(List<String> names) {
		this.names = Collections.unmodifiableList(names);
	}

	@Override
	public String toString() {
		return names.stream().collect(Collectors.joining(", "));
	}

	public List<String> getNames() {
		return names;
	}
}
