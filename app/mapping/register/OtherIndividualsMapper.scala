/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.Inject
import mapping.Mapping
import models.UserAnswers
import play.api.Logger

class OtherIndividualsMapper @Inject()(otherIndividualMapper: OtherIndividualMapper) extends Mapping[OtherIndividualsType] {

  override def build(userAnswers: UserAnswers): Option[OtherIndividualsType] = {

    val individual = otherIndividualMapper.build(userAnswers)
    val all= Seq(individual).flatten.flatten

    if (all.nonEmpty) {
      Some(
        OtherIndividualsType(
          otherIndividual = individual,
          otherIndividualCompany = None
        )
      )
    } else {
      Logger.info(s"[OtherIndividualsMapper][build] no otherIndividuals to map")
      None
    }
  }
}
