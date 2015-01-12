package sk.stuba.fiit.perconik.activity.listeners.refactor;

import com.google.common.base.Optional;

import org.eclipse.ltk.core.refactoring.history.RefactoringHistoryEvent;

import sk.stuba.fiit.perconik.activity.listeners.CommonEventListener;
import sk.stuba.fiit.perconik.core.annotations.Version;
import sk.stuba.fiit.perconik.eclipse.ltk.core.refactoring.history.RefactoringEventProxy;
import sk.stuba.fiit.perconik.eclipse.ltk.core.refactoring.history.RefactoringEventType;
import sk.stuba.fiit.perconik.eclipse.ltk.core.refactoring.history.RefactoringHistoryEventType;

import static java.util.Objects.requireNonNull;

import static sk.stuba.fiit.perconik.activity.listeners.refactor.RefactoringHistoryListener.Action.fromType;
import static sk.stuba.fiit.perconik.eclipse.ltk.core.refactoring.history.RefactoringHistoryEventType.ADDED;
import static sk.stuba.fiit.perconik.eclipse.ltk.core.refactoring.history.RefactoringHistoryEventType.DELETED;
import static sk.stuba.fiit.perconik.eclipse.ltk.core.refactoring.history.RefactoringHistoryEventType.POPPED;
import static sk.stuba.fiit.perconik.eclipse.ltk.core.refactoring.history.RefactoringHistoryEventType.PUSHED;

/**
 * TODO
 *
 * @author Pavol Zbell
 * @since 1.0
 */
@Version("0.0.0.alpha")
public final class RefactoringHistoryListener extends AbstractRefactoringListener implements sk.stuba.fiit.perconik.core.listeners.RefactoringHistoryListener {
  public RefactoringHistoryListener() {}

  enum Action implements CommonEventListener.Action {
    ADD(ADDED),

    REMOVE(DELETED),

    PUSH(PUSHED),

    POP(POPPED);

    private final String name;

    private final String path;

    RefactoringHistoryEventType type;

    private Action(final RefactoringHistoryEventType type) {
      this.name = actionName("eclipse", "refactor", "history", this);
      this.path = actionPath(this.name);
      this.type = requireNonNull(type);
    }

    static Optional<Action> fromType(final RefactoringEventType type) {
      for (Action action: values()) {
        if (action.type == type) {
          return Optional.of(action);
        }
      }

      return Optional.absent();
    }

    public String getName() {
      return this.name;
    }

    public String getPath() {
      return this.path;
    }
  }

  @Override
  Optional<Action> resolve(final RefactoringEventProxy<?> event) {
    return fromType(event.getType());
  }

  public void historyNotification(final RefactoringHistoryEvent event) {
    final long time = currentTime();

    this.execute(time, RefactoringEventProxy.wrap(event));
  }
}