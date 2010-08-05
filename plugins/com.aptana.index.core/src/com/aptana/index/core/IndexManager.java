package com.aptana.index.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class IndexManager
{
	private static IndexManager instance;
	private Map<URI, Index> indexes;

	static final ISchedulingRule MUTEX_RULE = new ISchedulingRule()
	{
		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule)
		{
			return rule == this;
		}
	};

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public synchronized static IndexManager getInstance()
	{
		if (instance == null)
			instance = new IndexManager();
		return instance;
	}

	/**
	 * IndexManager
	 */
	private IndexManager()
	{
		indexes = new HashMap<URI, Index>();
	}

	/**
	 * getIndex
	 * 
	 * @param path
	 * @return
	 */
	public synchronized Index getIndex(URI path)
	{
		Index index = indexes.get(path);
		if (index == null)
		{
			try
			{
				// First try to re-use an existing file if possible
				index = new Index(path, true);
				indexes.put(path, index);
			}
			catch (IOException e)
			{
				try
				{
					// We failed. Most likely disk index signature changed or got corrupted.
					// Don't re-use the file (create an empty index file) and then force a rebuild of the index.
					index = new Index(path, false);
					indexes.put(path, index);
					new RebuildIndexJob(path).schedule();
				}
				catch (IOException e1)
				{
					IndexActivator.logError("An error occurred while trying to access an index", e1); //$NON-NLS-1$
				}
			}
		}
		return index;
	}

	/**
	 * Removes the index for a given path. This is a no-op if the index did not exist.
	 */
	public synchronized void removeIndex(URI path)
	{
		Index index = getIndex(path);
		File indexFile = null;
		if (index != null)
		{
			index.monitor = null;
			indexFile = index.getIndexFile();
		}
		if (indexFile != null && indexFile.exists())
		{
			indexFile.delete();
		}
		this.indexes.remove(path);
	}
}
