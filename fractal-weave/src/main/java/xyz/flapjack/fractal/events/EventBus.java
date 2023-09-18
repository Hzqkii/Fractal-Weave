package xyz.flapjack.fractal.events;

/* Open. */
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<Class<? extends FractalEvent>, List<EventListenerWrapper>> eventListeners;

    public EventBus() {
        eventListeners = new HashMap<>();
    }

    /**
     * Subscribes to the annotated event.
     * @param subscriber the target event.
     */
    public void subscribe(final Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();
        Method[] methods = subscriberClass.getMethods();

        for (Method method: methods) {
            if (method.isAnnotationPresent(Subscribed.class) && method.getParameterCount() == 1) {
                Class<? extends FractalEvent> eventType = method.getAnnotation(Subscribed.class).eventType();
                if (FractalEvent.class.isAssignableFrom(eventType)) {
                    eventListeners.computeIfAbsent(eventType, key -> new ArrayList<>())
                            .add(new EventListenerWrapper(subscriber, method));
                }
            }
        }
    }

    /**
     * Unsubscribes from an event
     * @param subscriber the event.
     */
    public void unsubscribe(final Object subscriber) {
        for (List<EventListenerWrapper> wrappers: eventListeners.values()) {
            wrappers.removeIf(wrapper -> wrapper.subscriber == subscriber);
        }
    }

    /**
     * Calls the event to the all the subscribers.
     * @param event the event.
     */
    public void call(final FractalEvent event) {
        Class<? extends FractalEvent> eventClass = event.getClass();
        List<EventListenerWrapper> wrappers = eventListeners.get(eventClass);

        if (wrappers != null) {
            for (EventListenerWrapper wrapper: wrappers) {
                try {
                    wrapper.method.invoke(wrapper.subscriber, event);

                } catch (final Exception ignored) {
                    System.out.println(ignored);
                }
            }
        }
    }

    /**
     * A record to hold the event listener.
     * @param subscriber    the subscriber.
     * @param method        the method to hook.
     */
    private record EventListenerWrapper(Object subscriber, Method method) { }
}
