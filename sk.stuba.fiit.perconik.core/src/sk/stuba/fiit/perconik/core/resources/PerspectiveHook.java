package sk.stuba.fiit.perconik.core.resources;

import org.eclipse.ui.IWorkbenchWindow;

import sk.stuba.fiit.perconik.core.listeners.PerspectiveListener;
import sk.stuba.fiit.perconik.core.listeners.WindowListener;

final class PerspectiveHook extends InternalHook<IWorkbenchWindow, PerspectiveListener> implements WindowListener {
  PerspectiveHook(final PerspectiveListener listener) {
    super(new WindowHandler(listener));
  }

  static final class Support extends AbstractHookSupport<PerspectiveHook, IWorkbenchWindow, PerspectiveListener> {
    public Hook<IWorkbenchWindow, PerspectiveListener> create(final PerspectiveListener listener) {
      return new PerspectiveHook(listener);
    }
  }

  private static final class WindowHandler extends InternalHandler<IWorkbenchWindow, PerspectiveListener> {
    WindowHandler(final PerspectiveListener listener) {
      super(IWorkbenchWindow.class, listener);
    }

    public void register(final IWorkbenchWindow window) {
      window.addPerspectiveListener(this.listener);
    }

    public void unregister(final IWorkbenchWindow window) {
      window.removePerspectiveListener(this.listener);
    }
  }

  @Override
  void preRegisterInternal() {
    Hooks.addWindowsAsynchronouslyTo(this);
  }

  public void windowOpened(final IWorkbenchWindow window) {
    this.add(window);
  }

  public void windowClosed(final IWorkbenchWindow window) {
    this.remove(window);
  }

  public void windowActivated(final IWorkbenchWindow window) {}

  public void windowDeactivated(final IWorkbenchWindow window) {}
}
