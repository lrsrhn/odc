# Observable Tree Traverser

v Stop processing condition
v Support for pretty printed XML so that text with ignorable whitespaces are not send to the OTT
v unify wording for exppression builder with addelements addxpath stuff 
v ExpressionBuilder overhaul: Better support for relative and readRaw text (builder style like tree dsl)
v Go through and note stuff that needs refactoring
v Inline namespace predicate into DSL's 
v Missing handle all from expression build
v Test interchange expression builder reference to tree builder
v Rename ElementFinder to Node and SearchLocation to Edge.
v Refactor/move packages in each module

x Fix todos
x Be able to set depth for the xml builder

DOM and VTD implementations should implicitly return the DOM inside the ObjectStore. The DOM should be reused.

Make simple concurrency tests - Have a look at the CDA builder
