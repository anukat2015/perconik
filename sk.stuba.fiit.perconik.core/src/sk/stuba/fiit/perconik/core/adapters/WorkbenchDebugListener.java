package sk.stuba.fiit.perconik.core.adapters;

import org.eclipse.ui.IWorkbench;
import sk.stuba.fiit.perconik.core.Adapter;
import sk.stuba.fiit.perconik.core.listeners.WorkbenchListener;

public class WorkbenchDebugListener extends Adapter implements WorkbenchListener
{
	public boolean preShutdown(IWorkbench workbench, boolean forced)
	{
		return true;
	}

	public void postShutdown(IWorkbench workbench)
	{
	}
}
