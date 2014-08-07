/*******************************************************************************
 * Copyright (c) 2014 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jeff Martin - initial API and implementation
 ******************************************************************************/
package cuchaz.enigma.gui;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.beust.jcommander.internal.Lists;

import cuchaz.enigma.mapping.Ancestries;
import cuchaz.enigma.mapping.Translator;

public class ClassInheritanceTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 4432367405826178490L;
	
	private Translator m_deobfuscatingTranslator;
	private String m_obfClassName;
	
	public ClassInheritanceTreeNode( Translator deobfuscatingTranslator, String obfClassName )
	{
		m_deobfuscatingTranslator = deobfuscatingTranslator;
		m_obfClassName = obfClassName;
	}
	
	public String getObfClassName( )
	{
		return m_obfClassName;
	}
	
	public String getDeobfClassName( )
	{
		return m_deobfuscatingTranslator.translateClass( m_obfClassName );
	}
	
	@Override
	public String toString( )
	{
		String deobfClassName = getDeobfClassName();
		if( deobfClassName != null )
		{
			return deobfClassName;
		}
		return m_obfClassName;
	}
	
	public void load( Ancestries ancestries, boolean recurse )
	{
		// get all the child nodes
		List<ClassInheritanceTreeNode> nodes = Lists.newArrayList();
		for( String subclassName : ancestries.getSubclasses( m_obfClassName ) )
		{
			nodes.add( new ClassInheritanceTreeNode( m_deobfuscatingTranslator, subclassName ) );
		}
		
		// add then to this node
		for( ClassInheritanceTreeNode node : nodes )
		{
			this.add( node );
		}
		
		if( recurse )
		{
			for( ClassInheritanceTreeNode node : nodes )
			{
				node.load( ancestries, true );
			}
		}
	}
}