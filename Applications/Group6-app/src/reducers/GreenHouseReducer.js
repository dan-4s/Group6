import {GREENHOUSE_FETCH_SUCCESS} from '../components/actions/types'

const INITIAL_STATE={};

export default (state = INITIAL_STATE, action)=> {
    switch (action.type) {
        case GREENHOUSE_FETCH_SUCCESS:
            return action.payload;
        default:
            return state;
    }
}