package com.donglaistd.jinli.service;

import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.event.*;
import com.donglaistd.jinli.listener.*;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class EventPublisher {
    private static final Logger logger = Logger.getLogger(EventPublisher.class.getName());

    static public AtomicBoolean isEnabled = new AtomicBoolean(true);

    static EventExecutorGroup executor = new DefaultEventExecutorGroup(4);

    static {
        listenerMap = new ConcurrentHashMap<>();
        addListener(GameFinishEvent.class, SpringContext.getBean(GameFinishListener.class));
        addListener(UserDisconnectEvent.class, SpringContext.getBean(UserDisconnectListener.class));
        addListener(BankerQuitEvent.class, SpringContext.getBean(BankerQuitListener.class));
        addListener(RedPacketEndEvent.class, SpringContext.getBean(RedPacketEndListener.class));
        addListener(GiftEvent.class, SpringContext.getBean(GiftListener.class));
        addListener(LandlordsEndEvent.class, SpringContext.getBean(LandlordsEndListener.class));
        addListener(TexasEndEvent.class, SpringContext.getBean(TexasEndListener.class));
        addListener(AddTexasEvent.class, SpringContext.getBean(AddTexasListener.class));
        addListener(ModifyCoinEvent.class, SpringContext.getBean(ModifyCoinListener.class));
        addListener(GoldenFlowerEndEvent.class, SpringContext.getBean(GoldenFlowerEndListener.class));
        addListener(AddGoldenFlowerEvent.class, SpringContext.getBean(AddGoldenFlowerListener.class));
        addListener(TaskEvent.class, SpringContext.getBean(TaskEventListener.class));
        addListener(ModifyUserResourceEvent.class, SpringContext.getBean(ModifyUserResourceEventListener.class));
        addListener(LiveRecordEvent.class, SpringContext.getBean(LiveRecordListener.class));
    }

    static private final Map<Class<? extends BaseEvent>, List<EventListener>> listenerMap;

    static public void addListener(Class<? extends BaseEvent> clazz, EventListener listener) {
        listenerMap.computeIfAbsent(clazz, k -> new ArrayList<>()).add(listener);
    }

    static public void publish(BaseEvent event) {
        if (!isEnabled.get()) return;
        var listenerList = listenerMap.get(event.getClass());
        if (listenerList == null) {
            return;
        }
        for (var listener : listenerList) {
            try {
//                if (listener instanceof SyncEventListener) {
                var result = listener.handle(event);
                if (listener.isDisposable() && result) {
                    listenerList.remove(listener);
                }
//                } else {
//                    var result = executor.submit(() -> listener.handle(event));
//                    if (listener.isDisposable()) {
//                        var disposeListener = new FutureListener<Boolean>() {
//                            @Override
//                            public void operationComplete(Future<Boolean> future) {
//                                if (future.getNow()) {
//                                    logger.fine("remove listener:" + listener);
//                                    listenerList.remove(listener);
//                                }
//                            }
//                        };
//                        result.addListener(disposeListener);
//                    }
//                }
            } catch (Exception e) {
                logger.warning("exception catch with event:" + event.getClass());
                logger.warning(e.getMessage());
            }
        }
    }

    public static void removeListener(Class<? extends BaseEvent> event, SwitchGameListener switchGameListener) {
        var entry = listenerMap.get(event);
        if (entry != null) {
            entry.remove(switchGameListener);
        }
    }
}
