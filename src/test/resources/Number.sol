pragma solidity ^0.4;

contract Number{

    int256 number;

    function newNumber(int256 _number) public {
        number = _number;
    }

    function getNumber() public view returns (int256) {
        return number;
    }
}