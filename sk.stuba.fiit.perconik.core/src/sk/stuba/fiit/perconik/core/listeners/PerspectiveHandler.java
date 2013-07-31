package sk.stuba.fiit.perconik.core.listeners;

import org.eclipse.swt.widgets.Display;
import sk.stuba.fiit.perconik.eclipse.ui.Workbenches;

enum PerspectiveHandler implements Handler<PerspectiveListener>
{
	INSTANCE;
	
	public final void add(final PerspectiveListener listener)
	{
		final Runnable addListener = new Runnable()
		{
			@Override
			public final void run()
			{
				Workbenches.waitForActiveWindow().addPerspectiveListener(listener);
			}
		};
	
		Display.getDefault().asyncExec(addListener);
	}

	public final void remove(final PerspectiveListener listener)
	{
		final Runnable removeListener = new Runnable()
		{
			@Override
			public final void run()
			{
				Workbenches.waitForActiveWindow().removePerspectiveListener(listener);
			}
		};
	
		Display.getDefault().asyncExec(removeListener);
	}
}
