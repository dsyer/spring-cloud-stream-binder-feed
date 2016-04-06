Spring Cloud Stream binder for RSS feeds. An app that uses this binder
exposes an endpoints `/feeds/{destination}` which is an RSS feed. For
example, an app with `@EnableBinding(Source.class)` will have a live
feed at `/feeds/input`.

Each entry in the feed contains a link to a message, so consumers just
need to configure the feed url, as

```
spring:
  cloud:
    stream:
      feed:
        bindings:
          {input}:
            consumer:
              uri: http://localhost:8081/feeds/{destination}
```

where `{input}` is the name of the input binding (e.g. "input" for a
standard `Sink`), and `{destination}` is the name of the destination
(i.e. "output" for a standard `Source`).

The sample app uses Spring Cloud Bus to demonstrate the features. In
this case the input channel is `springCloudBusInput` and the
destination (by default) is `springCloudBus`. There are 2 profiles
("default" and "other") running on ports 8080 and 8081
respectively. You can test it by running the app in the two profiles
locally and POSTing to `/bus/refresh` on one of them. The refresh
event will show up in both apps, and the receiver will publish an ack.
