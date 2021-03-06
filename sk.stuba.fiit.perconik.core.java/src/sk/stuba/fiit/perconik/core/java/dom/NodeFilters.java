package sk.stuba.fiit.perconik.core.java.dom;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;

import org.eclipse.jdt.core.dom.ASTNode;

import sk.stuba.fiit.perconik.eclipse.jdt.core.dom.NodeType;

import static com.google.common.collect.Sets.immutableEnumSet;

public final class NodeFilters {
  private NodeFilters() {}

  private static abstract class AbstractNodeTypePredicate<N extends ASTNode> implements Predicate<N> {
    final Set<NodeType> types;

    AbstractNodeTypePredicate(final NodeType type, final NodeType ... rest) {
      this.types = immutableEnumSet(type, rest);
    }

    AbstractNodeTypePredicate(final Iterable<NodeType> types) {
      this.types = immutableEnumSet(types);
    }

    @Override
    public final int hashCode() {
      return this.getNodeTypes().hashCode();
    }

    final Set<NodeType> getNodeTypes() {
      return this.types;
    }
  }

  private static final class IsInstancePredicate<N extends ASTNode> extends AbstractNodeTypePredicate<N> {
    IsInstancePredicate(final NodeType type, final NodeType ... rest) {
      super(type, rest);
    }

    IsInstancePredicate(final Iterable<NodeType> types) {
      super(types);
    }

    public boolean apply(final N node) {
      for (NodeType type: this.types) {
        if (type.isInstance(node)) {
          return true;
        }
      }

      return false;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
      if (o instanceof IsInstancePredicate) {
        IsInstancePredicate<?> other = (IsInstancePredicate<?>) o;

        return this.getNodeTypes().equals(other.getNodeTypes());
      }

      return false;
    }

    @Override
    public String toString() {
      return "isInstance(" + Joiner.on(',').join(this.getNodeTypes()) + ")";
    }
  }

  private static final class IsMatchingPredicate<N extends ASTNode> extends AbstractNodeTypePredicate<N> {
    IsMatchingPredicate(final NodeType type, final NodeType ... rest) {
      super(type, rest);
    }

    IsMatchingPredicate(final Iterable<NodeType> types) {
      super(types);
    }

    public boolean apply(final N node) {
      for (NodeType type: this.types) {
        if (type.isMatching(node)) {
          return true;
        }
      }

      return false;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
      if (o instanceof IsMatchingPredicate) {
        IsMatchingPredicate<?> other = (IsMatchingPredicate<?>) o;

        return this.getNodeTypes().equals(other.getNodeTypes());
      }

      return false;
    }

    @Override
    public String toString() {
      return "isMatching(" + Joiner.on(',').join(this.getNodeTypes()) + ")";
    }
  }

  public static <N extends ASTNode> Predicate<N> isInstance(final NodeType type, final NodeType ... rest) {
    return new IsInstancePredicate<>(type, rest);
  }

  public static <N extends ASTNode> Predicate<N> isInstance(final Iterable<NodeType> types) {
    return new IsInstancePredicate<>(types);
  }

  public static <N extends ASTNode> Predicate<N> isMatching(final NodeType type, final NodeType ... rest) {
    return new IsMatchingPredicate<>(type, rest);
  }

  public static <N extends ASTNode> Predicate<N> isMatching(final Iterable<NodeType> types) {
    return new IsMatchingPredicate<>(types);
  }
}
