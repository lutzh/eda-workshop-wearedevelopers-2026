# Event-Driven Architecture Training

## 2-Day Course Agenda

---

## Day 1: Foundations and Design

| Time | Topic |
|------|-------|
| 09:00 -- 09:30 | **Welcome and kick-off** |
| 09:30 -- 10:30 | **Why Event-Driven Architecture?** -- The distributed monolith problem, synchronous dependencies and temporal coupling, breaking temporal coupling with events, "bring the data to the process." |
| 10:30 -- 10:45 | *Coffee break* |
| 10:45 -- 11:30 | **What Is EDA Today?** -- Defining contemporary event-driven architecture: microservices, eventual consistency, log-based brokers, macro- vs. microarchitecture, and what EDA is *not* about. |
| 11:30 -- 12:30 | **What's an Event?** -- Events as facts. Events vs. commands vs. queries. Fire-and-forget and the passive-aggressive commands antipattern. The dual nature of events as triggers and data carriers. |
| 12:30 -- 13:30 | *Lunch break* |
| 13:30 -- 14:15 | **Producer Responsibilities** -- Delivery guarantees, the dual write problem, the transactional outbox pattern, delivery ordering and partitioning. |
| 14:15 -- 15:00 | **Consumer Responsibilities** -- Error handling and blocking retry, processing order guarantees, idempotency, exactly-once processing, the idempotent receiver pattern. |
| 15:00 -- 15:15 | *Coffee break* |
| 15:15 -- 16:30 | **Event Design** -- Events as narrative, naming conventions, delta vs. wide vs. snapshot events, designing for idempotency and ordering, event metadata and envelopes, message keys, serialization formats. |
| 16:30 -- 17:00 | **Day 1 wrap-up** |

---

## Day 2: Real-World Challenges

| Time | Topic |
|------|-------|
| 09:00 -- 09:30 | **Day 2 kick-off and recap** |
| 09:30 -- 10:30 | **Complex Workflows** -- Orchestration vs. choreography, the Saga pattern, decomposing workflows, the Unix philosophy for services, observability vs. control. |
| 10:30 -- 10:45 | *Coffee break* |
| 10:45 -- 11:45 | **Advanced Patterns** -- Delayed reprocessing, retry topics and dead letter queues, Change Data Capture for the outbox, Bloom filters, timeouts and time-triggered events, the forward cache. |
| 11:45 -- 12:30 | **Integrating with the Outside World** -- Event-driven core with request-response shell, HTTP APIs, legacy system integration, CDC, publishing events externally, edge cases (dangling references, message size, request/response). |
| 12:30 -- 13:30 | *Lunch break* |
| 13:30 -- 14:15 | **Schema Evolution** -- Non-breaking vs. breaking changes, compatibility modes, rolling out breaking changes, bootstrapping new services.  |
| 14:15 -- 14:30 | **Stream Processing** -- Event streams for analytics and data lakes, stream processing concepts|
| 14:30 -- 15:30 | **Event Sourcing** -- Event sourcing as a microarchitecture pattern, how it relates to (and differs from) EDA. |
| 15:30 -- 15:45 | *Coffee break* |
| 15:45 -- 16:15 | **Operations and Observability** -- Logs, metrics, and traces for EDA, distributed tracing, correlation and causation IDs, consumer lag, scaling, schema registries. |
| 16:15 -- 16:45 | **Open Q&A** -- Ask me Anything. Open tickets from the parking lot. |
| 16:45 -- 17:00 | **Training wrap-up** |

---

## What to Expect

This is an interactive training. You will spend a significant part of the two days working in small groups on design exercises, discussing scenarios, and reviewing real-world examples. No coding is required, but we will look at code examples together.

Come prepared to share your own experiences with distributed systems. The exercises work best when you bring real challenges from your projects into the discussions.
