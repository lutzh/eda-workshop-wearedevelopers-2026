# Event-Driven Microservices: Patterns and Practices for Production-Ready Systems

## 1-Day Masterclass Agenda (6.5 hours)

*WeAreDevelopers 2026*

---

## Block I — Foundations & Event Design (10:00 - 12:30)

| Time | Topic |
|------|-------|
| 10:00 - 10:15 | **Welcome and kick-off** |
| 10:15 - 11:00 | **Why Event-Driven Architecture?** -- The distributed monolith problem, synchronous dependencies and temporal coupling, breaking temporal coupling with events, "bring the data to the process." |
| 11:00 - 11:30 | **What Is EDA Today?** -- Contemporary EDA: microservices, eventual consistency, log-based brokers (Kafka), macro- vs. microarchitecture, and what EDA is *not*. |
| 11:30 - 11:40 | *Short break* |
| 11:40 - 12:10 | **What's an Event?** -- Events as facts. Events vs. commands vs. queries. Fire-and-forget and the passive-aggressive commands antipattern. The dual nature of events as triggers and data carriers. |
| 12:10 - 12:30 | **Event Design (part 1)** -- Events as narrative, naming conventions, delta vs. wide vs. snapshot events. 

---

## Block II — Producing & Consuming Reliably (13:30 -- 15:30)

| Time | Topic |
|------|-------|
| 13:30 - 13:40 | **Recap & warm-up** |
| 13:40 - 14:10 | **Event Design (part 2)** -- Designing for idempotency and ordering, event metadata and envelopes, message keys, serialization formats. |
| 14:10 - 14:55 | **Producer Responsibilities** -- Delivery guarantees, the dual write problem, the transactional outbox pattern, delivery ordering and partitioning. |
| 14:55 - 15:30 | **Consumer Responsibilities** -- Error handling and blocking retry, processing order guarantees, idempotency, exactly-once processing, the idempotent receiver pattern. |

---

## Block III — Real-World Challenges & Operations (16:00 -- 18:00)

| Time | Topic |
|------|-------|
| 16:00 - 16:45 | **Complex Workflows** -- Orchestration vs. choreography, the Saga pattern, decomposing workflows, the Unix philosophy for services, observability vs. control. *Group exercise.* |
| 16:45 - 17:15 | **Advanced Patterns & Failure Handling** -- Retry topics and dead letter queues, delayed reprocessing, Change Data Capture for the outbox, timeouts and time-triggered events. |
| 17:15 - 17:40 | **Schema Evolution** -- Non-breaking vs. breaking changes, compatibility modes, rolling out breaking changes, schema registries. |
| 17:40 - 17:55 | **Operations & Observability** -- Logs, metrics, and traces for EDA, distributed tracing, correlation and causation IDs, consumer lag, scaling. |
| 17:55 - 18:00 | **Wrap-up & Open Q&A** -- Key takeaways, parking-lot tickets, Ask Me Anything. |

---

## What to Expect

This is an interactive workshop. You will spend a significant part of the day working in small groups on design exercises, discussing scenarios, and reviewing real-world examples. No coding is required, but we will look at code examples together. The workshop is technology-agnostic, using message brokers such as Apache Kafka to ground the concepts.

Come prepared to share your own experiences with distributed systems. The exercises work best when you bring real challenges from your projects into the discussions.

*Note: because this is a single day, some topics from the full two-day course — stream processing, event sourcing, and integration with legacy/outside systems — are touched on only briefly or left as follow-up reading.*
