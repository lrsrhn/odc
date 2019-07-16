# Observable Tree Traverser

v Stop processing condition
v Support for pretty printed XML so that text with ignorable whitespaces are not send to the OTT
x Be able to set depth for the xml builder
x unify wording for exppression builder with addelements addxpath stuff 
ExpressionBuilder overhaul: Better support for relative and readRaw text (builder style like tree dsl)
Go through and note stuff that needs refactoring

DOM and VTD implementations should implicitly return the DOM inside the ObjectStore. The DOM should be reused.

Make simple concurrency tests - Have a look at the CDA builder
