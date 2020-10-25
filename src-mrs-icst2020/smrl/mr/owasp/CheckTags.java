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
import smrl.mr.language.CollectionOfConcepts;
import smrl.mr.language.Operations;

import smrl.mr.language.MR;

@SuppressWarnings("all")
public class CheckTags extends MR{
  public boolean mr() {
    List<Action> _actions = Operations.Input(1).actions();
    for (final Action action1 : _actions) {
      List<Action> _actions_1 = Operations.Input(2).actions();
      for (final Action action2 : _actions_1) {
        for (int i = 0; (((((Operations.afterLogin(action1) && 
          Operations.afterLogin(action2)) && 
          action1.getUser().isSimilar(action2.getUser())) && 
          Operations.Output(Operations.Input(1), action1.getPosition()).containListOfTags()) && 
          Operations.Output(Operations.Input(2), action2.getPosition()).containListOfTags()) && 
          (i < Operations.Output(Operations.Input(1), action1.getPosition()).listsOfTags().size())); i++) {
          {
            CollectionOfConcepts listOfTags1 = Operations.Output(Operations.Input(1), action1.getPosition()).listsOfTags().get(i);
            List<CollectionOfConcepts> _listsOfTags = Operations.Output(Operations.Input(2), action2.getPosition()).listsOfTags();
            for (final CollectionOfConcepts listOfTags2 : _listsOfTags) {
              {
                ifThenBlock();
                if (((listOfTags1.equals(listOfTags2) && 
                  Operations.EQUAL(Operations.Input(3), Operations.changeCredentials(Operations.Input(1), Operations.User()))) && 
                  Operations.EQUAL(Operations.Input(4), Operations.changeCredentials(Operations.Input(2), Operations.User())))) {
                  ifThenBlock();
                  if (((Operations.isError(Operations.Output(Operations.Input(3), action1.getPosition())) || 
                    Operations.isError(Operations.Output(Operations.Input(4), action2.getPosition()))) || 
                    Operations.Output(Operations.Input(3), action1.getPosition()).listOfTags(listOfTags1.id).equals(
                      Operations.Output(Operations.Input(4), action2.getPosition()).listOfTags(listOfTags2.id)))) {
                    expressionPass(); /* //PROPERTY HOLDS" */
                  } else {
                    return Boolean.valueOf(false);
                  }
                } else {
                  expressionPass(); /* //PROPERTY HOLDS" */
                }
              }
            }
          }
        }
      }
    }
    return true;
  }
}
