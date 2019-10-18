import {combineReducers} from 'redux';
import AuthReducer from './AuthReducer'
import SensorDataReducer from './SensorDataReducer'
import GreenHouseReducer from './GreenHouseReducer'

export default combineReducers({
        auth: AuthReducer,
        sensorData:SensorDataReducer,
        greenHouses: GreenHouseReducer
    });
