package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.event.BaseEvent;

public interface EventListener {
    /**
     * @return true means need to remove this listener
     */
    boolean handle(BaseEvent event);

    /**
     * @return true if this listener only runs for one time, false if it need to stay as long time event processor
     */
    boolean isDisposable();
}
