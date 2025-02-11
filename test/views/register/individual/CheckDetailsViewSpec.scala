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

package views.register.individual

import viewmodels.AnswerSection
import views.behaviours.ViewBehaviours
import views.html.register.individual.CheckDetailsView

class CheckDetailsViewSpec extends ViewBehaviours {

  val index = 0
  val messageKeyPrefix = "otherIndividual.checkDetails"

  "CheckAnswers view" must {

    val view = viewFor[CheckDetailsView](Some(emptyUserAnswers))

    val applyView = view.apply(AnswerSection(None, Seq()), index, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView, messageKeyPrefix)

    behave like pageWithBackLink(applyView)
  }
}
