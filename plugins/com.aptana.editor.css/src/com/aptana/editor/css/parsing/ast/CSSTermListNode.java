package com.aptana.editor.css.parsing.ast;

public class CSSTermListNode extends CSSExpressionNode
{

	private String fSeparator;

	public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right)
	{
		this(left, right, null);
	}

	public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right, String separator)
	{
		super(left.getStart(), right.getEnd());
		setChildren(new CSSNode[] { left, right });
		fSeparator = separator;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(getChild(0));
		if (fSeparator == null)
		{
			text.append(" "); //$NON-NLS-1$
		}
		else
		{
			text.append(fSeparator);
		}
		text.append(getChild(1));
		return text.toString();
	}
}
