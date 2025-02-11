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

package controllers.register

import controllers.actions._
import controllers.actions.register.{OtherIndividualRequiredActionImpl, OtherIndividualRequiredRequest}
import forms.RemoveIndexFormProvider
import pages.QuestionPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.addAnother.OtherIndividualViewModel
import views.html.RemoveIndexView

import scala.concurrent.{ExecutionContext, Future}

trait RemoveIndexController extends FrontendBaseController with I18nSupport {
  val registrationsRepository: RegistrationsRepository
  val standardActionSets: StandardActionSets
  val otherIndividualAction: OtherIndividualRequiredActionImpl
  val formProvider: RemoveIndexFormProvider
  val controllerComponents: MessagesControllerComponents
  val view: RemoveIndexView
  implicit val ec: ExecutionContext

  private val prefix = "removeOtherIndividual"

  def otherIndividualAtIndex(index: Int): QuestionPage[OtherIndividualViewModel]

  def submitCall(index: Int, draftId: String): Call

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen otherIndividualAction(otherIndividualAtIndex(index), draftId)

  private def redirect(draftId: String): Result = Redirect(controllers.register.routes.AddOtherIndividualController.onPageLoad(draftId))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val form: Form[Boolean] = formProvider(prefix)

      Ok(view(form, draftId, index, name(request.otherIndividual), submitCall(index, draftId)))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val form: Form[Boolean] = formProvider(prefix)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, name(request.otherIndividual), submitCall(index, draftId)))),

        remove => {
          if (remove) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.deleteAtPath(otherIndividualAtIndex(index).path))
              _ <- registrationsRepository.set(updatedAnswers)
            } yield redirect(draftId)
          } else {
            Future.successful(redirect(draftId))
          }
        }
      )
  }

  private def name(otherIndividual: OtherIndividualViewModel)(implicit request: OtherIndividualRequiredRequest[AnyContent]): String = {
    otherIndividual.displayName match {
      case Some(name) => name
      case None => Messages(s"$prefix.default")
    }
  }
}
