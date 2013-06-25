cub
===

This library provides a tool and an API to perform pseudolocalization.
Pseudolocalization generates a fake translation of messages of a program,
which helps to highlight weaknesses and bugs in the original program regarding
localization.

The library includes a structured message API to allow it to be used for
complex multi-part messages, and includes the following pseudolocalization
methods:

  - accenter: replaces US-ASCII characters with accented versions, to make
    it obvious if parts of the output are hard-coded in the program and can't
    be localized
  - brackets: adds [brackets] around each message, to show where messages
    have been concatenated together. This is a localization problem because
    some languages may need to reorder phrases or the translation may change
    depending on what is around it.
  - expander: makes each message longer, to show where the UI doesn't give
    enough space for languages that result in longer strings, and either
    wraps awkwardly or truncates.
  - fakebidi: produces fake Right-to-Left text, using the original source
    text and wrapping LTR text with RTL markers, so that it renders as if it
    were RTL text but is still mostly readable to someone who doesn't speak
    Arabic or Hebrew.

These methods can be combined in any order and with user-written methods. In
addition, HTML tags can optionally be preserved (it is not recommended to give
them to the translator, but especially simple tags show up in translatable
text frequently).

These can also be accessed via locale variant subtags, which we hope to get
standardized. A variant subtag of psaccent corresponds to accenter, expander,
and brackets (in that order), and a variant subtag of psbidi corresponds to
fakebidi.

Usage
=====

To pseudo-localize a Macintosh or iPhone `.strings` file:

    mvn clean package && java -jar target/cub-1.0-SNAPSHOT.jar --method=html,accents,
brackets --type=strings <path to Localizable.string>

To pseudo-localize an Android `.xml` file:

    mvn clean package && java -jar target/cub-1.0-SNAPSHOT.jar --method=html,accents,
brackets --type=xml <path to strings.xml>

Additional Credits
==================
The original implementations this library is based upon were written by
Jerome Flesch while an intern at Google. This project is a fork of Google's
[pseudolocalization-tool](https://code.google.com/p/pseudolocalization-tool).
