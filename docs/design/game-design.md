# Boggle Game Architecture

The vision for this project was to be able to play boggle online with 
friends in new game modes that are simply not possible outside of a computer.
Some ideas where **multiplier tiles** , **decaying tiles**, and **wildcards**.

The intent was to be able to play these modes on the desktop or through mobile
which have different preferred input mechanism **path drawing** and **text**.
**text** support must be supported because **I want** to be able to type my answers
this would be a massive improvement to vanilla boggle because I can type much faster than write.

With mobile, typing would be clunky so naturally I must support some sort of 
touch interface.

---

## Game mode difficulties

Some game modes make using a common interface challenging.

> Wildcard game modes are path agnostic

- Wildcard need to be expanded, so a path converts to a word
- Text interface is natural for this mode and requires no expansion.

* Wildcard mode is [Path Agnostic] 
    - words are scored based on the text

> Decaying game modes are path sensitive

- The touch interface is natural for path sensitive game modes
    as it produces a path, no resolution needed.
- Text interface  could yield a valid word, but maps to many paths, need some
    mechanism to select a path that resolves to that word.

## Critical Takeaways
- Game modes can be broken down into two classifications
    - Path Sensitive
    - Path Agnostic
- Touch interface can easily resolves to a word through token expansion
- Text interface can resolve to a word by providing a selection.
    - This resolution only feels good if the choices (revealed)
        are invariant in points.
        - Decaying path choice is fine because it adds strategy
        - Boards with multiplier tiles are poor because users might be
            led to a higher scoring path then the one conceptualized.

## Implications

This likely means we need to model games depending on the game type.

For path aware games we submit paths and store paths
For path agnostic games we submit words and store words

