/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
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
package io.gravitee.rest.api.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.gravitee.rest.api.model.annotations.ParameterKey;
import io.gravitee.rest.api.model.parameters.Key;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsoleAuthentication extends CommonAuthentication {

    @ParameterKey(Key.CONSOLE_AUTHENTICATION_LOCALLOGIN_ENABLED)
    private Enabled localLogin;

    @ParameterKey(Key.EXTERNAL_AUTH_ENABLED)
    private Enabled externalAuth;

    @ParameterKey(Key.EXTERNAL_AUTH_ACCOUNT_DELETION_ENABLED)
    private Enabled externalAuthAccountDeletion;

    public Enabled getLocalLogin() {
        return localLogin;
    }

    public void setLocalLogin(Enabled localLogin) {
        this.localLogin = localLogin;
    }

    public Enabled getExternalAuth() {
        return externalAuth;
    }

    public void setExternalAuth(Enabled externalAuth) {
        this.externalAuth = externalAuth;
    }

    public Enabled getExternalAuthAccountDeletion() {
        return externalAuthAccountDeletion;
    }

    public void setExternalAuthAccountDeletion(Enabled externalAuthAccountDeletion) {
        this.externalAuthAccountDeletion = externalAuthAccountDeletion;
    }
}
