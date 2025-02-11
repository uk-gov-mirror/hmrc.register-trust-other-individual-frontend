/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.register

import mapping.reads.OtherIndividuals
import models.{OtherIndividualType, UserAnswers}

class OtherIndividualMapper {

  def build(userAnswers: UserAnswers): Option[List[OtherIndividualType]] = {

    userAnswers.get(OtherIndividuals) match {
      case None => None
      case Some(list) => Some(
        list.map { otherIndividual =>
          OtherIndividualType(
            name = otherIndividual.name,
            dateOfBirth = otherIndividual.dateOfBirth,
            identification = otherIndividual.identification,
            countryOfResidence = otherIndividual.countryOfResidence,
            nationality = otherIndividual.countryOfNationality,
            legallyIncapable = otherIndividual.mentalCapacityYesNo.map(!_)
          )
        }
      )
    }
  }
}
