package com.ecit.common.rx;

import com.ecit.common.constants.Constants;
import com.ecit.common.enums.EventBusStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

public class EventBusRxVerticle extends AbstractVerticle {

    protected JsonObject resultEventBus(EventBusStatus status, Object body){
        return new JsonObject().put(Constants.STATUS, status.getStatus()).put(Constants.BODY, body);
    }
}
