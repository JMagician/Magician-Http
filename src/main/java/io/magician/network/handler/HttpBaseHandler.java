package io.magician.network.handler;

import io.magician.application.request.MagicianRequest;
import io.magician.application.request.MagicianResponse;

/**
 * http handler interface
 */
public interface HttpBaseHandler {

    void request(MagicianRequest request, MagicianResponse response);
}
