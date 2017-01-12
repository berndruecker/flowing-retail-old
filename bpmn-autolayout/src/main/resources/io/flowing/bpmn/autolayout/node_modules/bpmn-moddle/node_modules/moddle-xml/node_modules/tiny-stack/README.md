# Tiny Stack

Stack micro library for the browser or server. When loaded as a script tag on the browser, Tiny Stack be available as 'stack()'.

[![build status](https://secure.travis-ci.org/avoidwork/tiny-stack.png)](http://travis-ci.org/avoidwork/tiny-stack)

## API
#### clear
Clears the stack

#### length
Gets the length/size of the stack

#### peek
Gets the top item of the stack

#### pop
Gets & removes the top item of the stack

#### push
Adds an item to the top the stack

## Example
```
var stack   = require("tiny-stack"),
    mystack = stack();


mystack.length(); // 0
mystack.push({name: "John Doe"});
mystack.push({name: "Jane Doe"});
mystack.length(); // 2
mystack.peek(); // {name: "Jane Doe"}
mystack.pop();
mystack.length(); // 1
mystack.peek(); // {name: "John Doe"}
mystack.clear();
mystack.length(); // 0
```
