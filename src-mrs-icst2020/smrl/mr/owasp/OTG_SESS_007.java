/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2020
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Xuan Phu MAI (xuanphu.mai@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package smrl.mr.owasp;

import java.util.List;
import smrl.mr.language.Action;
import smrl.mr.language.Operations;

import smrl.mr.language.MR;

@SuppressWarnings("all")
public class OTG_SESS_007 extends MR{
  public boolean mr() {
    List<Action> _actions = Operations.Input(1).actions();
    for (final Action action : _actions) {
      {
        ifThenBlock();
        if ((((Operations.notAvailableWithoutLoggingIn(action) && 
          Operations.NOT(Operations.NULL(Operations.Output(Operations.Input(1), action.getPosition()).getSession()))) && 
          (Operations.Output(Operations.Input(1), action.getPosition()).getSession().getTimeout() > 0)) && 
          Operations.EQUAL(Operations.Input(2), 
            Operations.addAction(Operations.Input(1), action.getPosition(), Operations.Wait(Operations.Output(Operations.Input(1), action.getPosition()).getSession().getTimeout()))))) {
          ifThenBlock();
          boolean _different = Operations.different(
            Operations.Output(Operations.Input(1), action.getPosition()), 
            Operations.Output(Operations.Input(2), action.getPosition()));
          if (_different) {
            expressionPass(); /* //PROPERTY HOLDS" */
          } else {
            return Boolean.valueOf(false);
          }
        } else {
          expressionPass(); /* //PROPERTY HOLDS" */
        }
      }
    }
    return true;
  }
}
