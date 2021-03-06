package org.w01f.dds.layer2.sql.parser.mysql.tree;

public class ElementListFactorNode extends ElementNode  implements Cloneable {
	private final ElementListNode elementList;

	@Override
	public ElementListFactorNode clone() {
		return new ElementListFactorNode(elementList == null ? null : elementList.clone());
	}

	public ElementListFactorNode(ElementListNode elementList) {
		this.elementList = elementList;

		super.setParent(elementList);
	}

	@Override
	public String toString() {
		return "(" + elementList + ")";
	}
}
