![Open in Codespaces](https://classroom.github.com/assets/open-in-codespaces-abfff4d4e15f9e1bd8274d9a39a0befe03a0632bb0f153d0ec72ff541cedbe34.svg)
# Assignment 3 Documentation

Author: Eric Kunzel

## Project Introduction

My task was to modify a parser so that additional tokens representing the programming language x could be incorporated into an existing grammar. This parser creates an abstract syntax tree that represents the hierarchy of the syntax of the language. 2 new visitors were then created to traverse the AST and print out the hierarchy.

## Execution and Development Environment

Java version: 19.0.1
Development Environment: Visual Studio Code

### Class Diagram

https://lucid.app/lucidchart/081e022a-5d09-4dde-8c94-c2410c869a43/edit?viewport_loc=-576%2C-246%2C2946%2C1743%2C0_0&invitationId=inv_560fc4a4-fd0d-4ffe-954e-eab86689eb86

## Scope of work and Project Discussion

1. task 1 was to update our parser to be able to understand new syntax using new and existing tokens. This was done by creating new programming that could handle new tokens inside the parsers recursive functions. These functions are what makes teh grammar. Two new recursive functions were created for forall statements, but the other tokens were dealt with just y adding new programming into the existing recursive functions. also new ast nodes had to be created to hold these new tokens.

2.Deleting all unnecesary test statements, not very dificult.

3. Task 3 was to create 2 new visitors that would create an image of the ast with the nodes centered on their parent. The first visitor is to find the coordinates on a grid, and the second visitor is to take those coordinates and draw them into a document using existing librarys for creating diagrams.

## Results and Conclusions
I can see why trees are integral to syntax. I spoke to a friend who is a linguist and he told me AST are how all languages are dechiphered with computers. Learning aout these trees and how they are manipulated is very important it seems. The first part of the project went well, I was getting the output I wanted, but the output test did not work for me. I tried for a while but I could not get it to pass despite identical output. It is saying that there is a capital A instead of a lower case one but theres nowhere in my file that even contains a capital a. My output is correct in this portion.

The second portion was harder for sure. I elieve I understand what to do and what is going on, and i was able to get it to work with simple.x, However when a larger text file with more code is executed, there are some errors in the child nodes. The top parent nodes still seem to organize correctly, but something happens with the child nodes. I dont think it is a prolem with the offset visitor, I think it may e in the drawOffsetVisitor

### What I Learned
I learned alot about how a computer would go aout translating a language. A computer language yes, ut also spoken languages. You could build an astract syntax tree that identifies words based on their part of speech and create a grammar to check the syntax. im sure this is the asis for spelling and grammar checkers in text processing of all types. I also learned a lot more aout recursive functions and how to traverse and process trees
### What I Could Do Better
I felt pretty good on this project and I took away a lot, again I could have started sooner and had my tree looking better and the one output test ug could have een worked out. I need to have these done far in advance so that I can really iron out these types of things. Again, I have no idea why it is not passing, but im sure I could have worked it out with more focus on it, I had to move on from it ecause of time, my fault.
I also realized I had een testing the draw offset on simple.x, and when i tried it with a larger peice of code there were errors in the drawing, I have a function that printed out the offset collection from offset visitor, and I couldnt find errors in the calculation of the offsets, maybe I am wrong. I think my errors were in the printing of the collection, but I am not sure.
### Challenges I Encountered
The main challenge I encountered was the Output test. I doule and triple checked my output and could find no difference etween the expected output from the tests and my output.
After getting further, as stated aove, I realized there was errors in the printout of larger text files, I ran out of time and couldnt locate the reason, but i dont think it is in offset visitor, I looked at each offset individually using the print offset function and it was ok in the stored hashmap