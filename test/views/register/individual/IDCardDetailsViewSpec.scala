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

import forms.PassportOrIdCardFormProvider
import models.{FullName, PassportOrIdCardDetails}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.QuestionViewBehaviours
import views.html.register.individual.IDCardDetailsView

class IDCardDetailsViewSpec extends QuestionViewBehaviours[PassportOrIdCardDetails] {

  private val messageKeyPrefix = "otherIndividual.idCardDetails"
  private val index = 0
  private val name = FullName("First", Some("Middle"), "Last")

  override val form = new PassportOrIdCardFormProvider(frontendAppConfig)(messageKeyPrefix)

  "IDCardDetailsView" must {

    val view = viewFor[IDCardDetailsView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, name.toString, index, draftId)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithPassportOrIDCardDetailsFields(
      form,
      applyView,
      messageKeyPrefix,
      controllers.register.individual.routes.IDCardDetailsController.onSubmit(0, "draftId").url,
      Seq(("country", None), ("number", None)),
      "expiryDate",
      name.toString
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
