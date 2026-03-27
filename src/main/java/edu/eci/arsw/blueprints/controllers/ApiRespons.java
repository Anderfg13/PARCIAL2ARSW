package edu.eci.arsw.blueprints.controllers;

public record ApiRespons<T>(int code, String message, T data) {}
