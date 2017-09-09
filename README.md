# Examples for Actoverse-Scala

## Provided Examples

- Two-Phase Commit Protocol
- InterestCalculator (Derived from [1], "Motivating Example")
- Online Store (including atomic violations)
- AsyncAnd (broken; Derived from [2])

## How to run

Requirements: Scala 2.11.8+, sbt 0.13.15+

1. Clone this repository
2. Run the command `sbt run`
3. Then you can debug via Actoverse with the address `localhost:3000`.

Tips: If you use `0.0.0.0` instead of `localhost` (you can change it in `src/main/recources/application.conf`), Actoverse can access from outside your machine.

## Bibliography

1. A. Lienhard, J. Fierz, and O. Nierstrasz, “Flow-centric, back-in-time debugging,” in Lecture Notes in Business Information Processing, 2009, vol. 33 LNBIP, pp. 272–288.
2. T. Stanley, T. Close, and M. S. & Miller, “Causeway : A message-oriented distributed debugger“ Technical Report. HP Labs. HP Labs tech report HPL-2009-78.