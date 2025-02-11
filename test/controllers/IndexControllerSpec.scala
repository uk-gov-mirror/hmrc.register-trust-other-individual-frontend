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

package controllers

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.{FullName, UserAnswers}
import org.mockito.ArgumentCaptor
import pages.register.individual.NamePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService
import play.api.inject.bind
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.Matchers.any

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val featureFlagService: FeatureFlagService = mock[FeatureFlagService]
  private val submissionDraftConnector: SubmissionDraftConnector = mock[SubmissionDraftConnector]

  "Index Controller" must {

    "pre-existing user answers" must {

      "redirect to add-to page if there is at least one in-progress or completed other individual" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(NamePage(0), name).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).get mustBe controllers.register.routes.AddOtherIndividualController.onPageLoad(fakeDraftId).url

        application.stop()
      }

      "redirect to trust has other individuals yes no page if there are no in-progress or completed other individuals" in {

        val userAnswers: UserAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).get mustBe controllers.register.routes.TrustHasOtherIndividualYesNoController.onPageLoad(fakeDraftId).url

        application.stop()
      }

      "update value of is5mldEnabled and isTaxable in user answers" in {

        reset(registrationsRepository)

        val userAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
        when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))
        when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        route(application, request).value.map { _ =>
          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

          uaCaptor.getValue.is5mldEnabled mustBe true
          uaCaptor.getValue.isTaxable mustBe true

          application.stop()
        }
      }

    }

    "no pre-existing user answers" must {

      "instantiate new set of user answers" when {

        "5mld enabled" in {

          reset(registrationsRepository)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
            .build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
          when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
          when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))
          when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(false))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          route(application, request).value.map { _ =>
            val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

            uaCaptor.getValue.is5mldEnabled mustBe true
            uaCaptor.getValue.is5mldEnabled mustBe false
            uaCaptor.getValue.draftId mustBe fakeDraftId
            uaCaptor.getValue.internalAuthId mustBe "id"

            application.stop()
          }

        }

        "5mld not enabled" in {

          reset(registrationsRepository)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
            .build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
          when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
          when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
          when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          route(application, request).value.map { _ =>
            val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

            uaCaptor.getValue.is5mldEnabled mustBe false
            uaCaptor.getValue.isTaxable mustBe true
            uaCaptor.getValue.draftId mustBe fakeDraftId
            uaCaptor.getValue.internalAuthId mustBe "id"

            application.stop()
          }

        }
      }

    }

  }
}
