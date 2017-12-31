package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Queue;

/**
 * Created by BB on 27/12/2017.
 */
public class AnimationStateComponent extends Component {

    public IntMap<AnimationState> drawableIdAnimationStateMap = new IntMap<AnimationState>();

    public AnimationStateComponent (){}

    public AnimationStateComponent put(int drawableIdentifier, int defaultAnimationState){
        drawableIdAnimationStateMap.put(drawableIdentifier, new AnimationState(defaultAnimationState));
        return this;
    }


    public class AnimationState {

        private int defaultState = 0;
        private int currentState = 0;

        private Queue<Integer> stateQueue = new Queue<Integer>();

        public float stateTime = 0.0f;

        public AnimationState(){
            defaultState = 0;
        }

        public AnimationState(int startState){
            defaultState = startState;
        }

        public int getDefaultState() {
            return defaultState;
        }

        public void setDefaultState(int defaultState) {
            this.defaultState = defaultState;
        }

        public void setCurrentState(int currentState) {
            this.currentState = currentState;
            this.stateTime = 0.0f;
        }

        public Queue<Integer> getStateQueue() {
            return stateQueue;
        }

        public void queueAnimationState(int state){
            if(stateQueue.size != 0){
                if(stateQueue.first() == state) this.stateTime = 0.0f;
            } else {
                stateQueue.addFirst(state);
            }
        }

        public int getCurrentState() {
            return currentState;
        }

    }

}
