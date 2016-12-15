/*
 * MergeHero: Differing and Merging Folders & Files
 *
 * Copyright © 2004, Dynamsoft, Inc. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.opensource.org/licenses/gpl-3.0.html.
 */

/*
 * Created on 2006-4-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package MergeHeroLib;

import java.util.ArrayList;

/**
 * @author Falcon Young
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CDiffMergeBlockArray extends ArrayList
{
//	override the parent member functio
	public void add(int iIndex, Object SMergedBlockToAdd) //insert
	{
		if (SMergedBlockToAdd instanceof SDiffMergeBlock)
			super.add(iIndex, SMergedBlockToAdd);
	}
	
	public boolean add(Object SMergedBlockToAdd)
	{
		if (SMergedBlockToAdd instanceof SDiffMergeBlock)
			return super.add(SMergedBlockToAdd);
		else
			return false;
	}
	
	public Object set(int iIndex, Object SMergedBlockToAdd)
	{
		if (SMergedBlockToAdd instanceof SDiffMergeBlock)
			return super.set(iIndex, SMergedBlockToAdd);
		else
			return null;
	}
}
