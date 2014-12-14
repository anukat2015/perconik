package sk.stuba.fiit.perconik.core.preferences;

import java.util.Map;

import com.google.common.collect.ForwardingMap;

import static java.util.Collections.EMPTY_MAP;

import static com.google.common.collect.Maps.newHashMap;

public abstract class AbstractConfiguration extends ForwardingMap<String, Object> implements Configuration {
  protected final Map<String, Object> options;

  protected AbstractConfiguration() {
    this(EMPTY_MAP);
  }

  protected AbstractConfiguration(final Map<String, Object> options) {
    this.options = newHashMap(options);
  }

  @Override
  protected final Map<String, Object> delegate() {
    return this.options;
  }

  @Override
  public abstract Object put(final String key, final Object value);

  @Override
  public final void putAll(final Map<? extends String, ?> map) {
    this.standardPutAll(map);
  }

  public final void fromMap(final Map<String, Object> map) {
    this.putAll(map);
  }

  public final Map<String, Object> toMap() {
    return newHashMap(this);
  }
}
