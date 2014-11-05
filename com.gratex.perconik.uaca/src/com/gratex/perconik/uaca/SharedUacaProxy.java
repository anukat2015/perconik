package com.gratex.perconik.uaca;

import java.net.URL;

import javax.annotation.Nullable;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import com.gratex.perconik.uaca.preferences.UacaPreferences;

import static java.lang.String.format;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;

public class SharedUacaProxy extends AbstractUacaProxy {
  public SharedUacaProxy() {}

  public static final void checkConnection(final String url) {
    newClient().target(url).path("ide/checkin").request().options().close();
  }

  public static final void checkConnection(final URL url) {
    checkConnection(url.toString());
  }

  @Override
  protected URL url() {
    return UacaPreferences.getShared().getUacaUrl();
  }

  @Override
  protected void filterRequest(final WebTarget target, @Nullable final Object request) {
    UacaReporter.logRequest(target, request);
  }

  @Override
  protected void processResponse(final WebTarget target, @Nullable final Object request, final Response response) {
    StatusType status = response.getStatusInfo();
    Family family = status.getFamily();

    if (family == CLIENT_ERROR || family == SERVER_ERROR) {
      String message = format("UacaProxy: POST %s -> %s %d %s", target.getUri(), family, status.getStatusCode(), status.getReasonPhrase());

      throw new IllegalStateException(message);
    }
  }

  @Override
  protected void reportFailure(final String message, final Exception failure) {
    UacaReporter.logError(message, failure);
    UacaReporter.displayError(message, failure);
  }
}
