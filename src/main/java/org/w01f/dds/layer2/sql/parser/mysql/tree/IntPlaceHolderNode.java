package org.w01f.dds.layer2.sql.parser.mysql.tree;

public class IntPlaceHolderNode extends SQLSyntaxTreeNode implements Cloneable  {

    private String intText;
    private ElementPlaceholderNode placeholderNode;

    @Override
    public IntPlaceHolderNode clone() {
        if (intText != null) return new IntPlaceHolderNode(intText);
        return new IntPlaceHolderNode(placeholderNode.clone());
    }

    public IntPlaceHolderNode(ElementPlaceholderNode placeholderNode) {
        this.placeholderNode = placeholderNode;
    }

    public IntPlaceHolderNode(String intText) {
        this.intText = intText;
    }

    @Override
    public String toString() {
        return intText != null ? intText : placeholderNode.toString();
    }
}