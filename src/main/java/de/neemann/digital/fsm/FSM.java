/*
 * Copyright (c) 2018 Helmut Neemann.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.fsm;

import de.neemann.digital.analyse.TruthTable;
import de.neemann.digital.analyse.expression.Expression;
import de.neemann.digital.analyse.expression.ExpressionException;
import de.neemann.digital.draw.graphics.Graphic;
import de.neemann.digital.draw.graphics.Vector;
import de.neemann.digital.draw.graphics.VectorFloat;
import de.neemann.digital.lang.Lang;

import java.util.ArrayList;
import java.util.List;

import static de.neemann.digital.draw.shapes.GenericShape.SIZE;

/**
 * A simple finite state machine
 */
public class FSM {

    private ArrayList<State> states;
    private ArrayList<Transition> transitions;

    /**
     * Creates a new FSM containing the given states
     *
     * @param state the states
     */
    public FSM(State... state) {
        states = new ArrayList<>();
        transitions = new ArrayList<>();
        for (State s : state)
            add(s);
    }

    /**
     * Adds a state to the FSM
     *
     * @param state the state to add
     * @return this for chained calls
     */
    public FSM add(State state) {
        if (state.getNumber() < 0)
            state.setNumber(states.size());
        states.add(state);
        return this;
    }

    /**
     * Adds a transition to the FSM
     *
     * @param transition the transition to add
     * @return this for chained calls
     */
    public FSM add(Transition transition) {
        transitions.add(transition);
        return this;
    }

    /**
     * Adds a transition to the FSM
     *
     * @param from      the from state
     * @param to        the to state
     * @param condition the condition
     * @return this for chained calls
     * @throws FinitStateMachineException FinitStateMachineException
     */
    public FSM transition(String from, String to, Expression condition) throws FinitStateMachineException {
        return transition(findState(from), findState(to), condition);
    }

    /**
     * Adds a transition to the FSM
     *
     * @param from      the from state
     * @param to        the to state
     * @param condition the condition
     * @return this for chained calls
     * @throws FinitStateMachineException FinitStateMachineException
     */
    public FSM transition(int from, int to, Expression condition) throws FinitStateMachineException {
        return transition(findState(from), findState(to), condition);
    }

    /**
     * Adds a transition to the FSM
     *
     * @param from      the from state
     * @param to        the to state
     * @param condition the condition
     * @return this for chained calls
     */
    public FSM transition(State from, State to, Expression condition) {
        if (!states.contains(from))
            states.add(from);
        if (!states.contains(to))
            states.add(to);
        return add(new Transition(from, to, condition));
    }

    private State findState(String name) throws FinitStateMachineException {
        for (State s : states)
            if (s.getName().equals(name))
                return s;
        throw new FinitStateMachineException(Lang.get("err_fsmState_N_notFound!", name));
    }

    private State findState(int number) throws FinitStateMachineException {
        for (State s : states)
            if (s.getNumber() == number)
                return s;
        throw new FinitStateMachineException(Lang.get("err_fsmState_N_notFound!", number));
    }

    /**
     * Calculates all forces to move the elements
     *
     * @return this for chained calls
     */
    public FSM calculateForces() {
        for (State s : states)
            s.calcExpansionForce(states);
        for (Transition t : transitions)
            t.calcForce(states, transitions);
        return this;
    }

    /**
     * @return the states
     */
    public List<State> getStates() {
        return states;
    }

    /**
     * Draws the FSM
     *
     * @param gr the Graphic instance to draw to
     */
    public void drawTo(Graphic gr) {
        for (State s : states)
            s.drawTo(gr);
        for (Transition t : transitions)
            t.drawTo(gr);
    }

    /**
     * Moved the elements
     *
     * @param dt         the time step
     * @param moveStates if true also states are moved
     * @param except     element which is fixed
     */
    public void move(int dt, boolean moveStates, Movable except) {
        calculateForces();
        if (moveStates)
            for (State s : states)
                if (s != except)
                    s.move(dt);
        for (Transition t : transitions)
            if (t != except)
                t.move(dt);
    }

    /**
     * Orders all states in a big circle
     */
    public void circle() {
        double delta = 2 * Math.PI / states.size();
        double rad = 0;
        for (State s : states)
            if (s.getRadius() > rad)
                rad = s.getRadius();

        rad *= 4;
        double phi = 0;
        for (State s : states) {
            s.setPosition(new VectorFloat((float) (Math.sin(phi) * rad), (float) (-Math.cos(phi) * rad)));
            phi += delta;
        }

        for (Transition t : transitions)
            t.initPos();
    }

    /**
     * Creates the truth table which is defined by this finite state machine
     *
     * @return the truth table
     * @throws ExpressionException        ExpressionException
     * @throws FinitStateMachineException FinitStateMachineException
     */
    public TruthTable createTruthTable() throws ExpressionException, FinitStateMachineException {
        return new TransitionTableCreator(states, transitions).create();
    }

    /**
     * Returns the element at the given position
     *
     * @param pos the position
     * @return the element or null
     */
    public Movable getMovable(Vector pos) {
        for (State s : states)
            if (s.matches(pos))
                return s;

        for (Transition t : transitions)
            if (t.matches(pos))
                return t;

        return null;
    }

    /**
     * Move states to raster
     */
    public void toRaster() {
        for (State s : states)
            s.setPosition(new VectorFloat((int) Math.round((double) s.getPos().getX() / SIZE) * SIZE,
                    (int) Math.round((double) s.getPos().getY() / SIZE) * SIZE));
    }
}