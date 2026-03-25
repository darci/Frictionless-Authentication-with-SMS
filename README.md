# 📱 Frictionless Authentication with SMS

> **POC** – Prova de conceito de autenticação por SMS com preenchimento automático de número de telefone utilizando a [Phone Number Hint API](https://developers.google.com/identity/phone-number-hint/overview) do Google Play Services.

---

## 🎯 Objetivo

Demonstrar um fluxo de autenticação simplificado onde o número de telefone do usuário é sugerido automaticamente pelo sistema operacional (sem necessidade de permissões), reduzindo o atrito no cadastro/login.

## ✨ Funcionalidades

- **Phone Number Hint API** – Exibe um bottom-sheet nativo do Android para o usuário selecionar seu número de telefone, sem solicitar permissão `READ_PHONE_STATE`
- **Validação de telefone** – Aceita formato internacional (`+5511999998888`), com validação de comprimento e caracteres
- **Preenchimento automático** – O campo de telefone é preenchido automaticamente com o número selecionado no picker
- **UI em Jetpack Compose** – Interface Material 3 com tema dinâmico (Dynamic Color no Android 12+)

## 🎬 Demo

[Screen_recording_20260324_205906.webm](https://github.com/user-attachments/assets/fc9f7efa-9cd2-4919-ba87-7e058ece6a5a)

> Vídeo demonstrando o fluxo completo: abertura do Phone Hint picker → seleção do número → preenchimento automático do campo.

## 🏗️ Arquitetura

O projeto segue **MVVM + Clean Architecture** com separação em 3 camadas:

```
app/src/main/java/io/github/darci/smsauthentication/
│
├── MainActivity.kt                          # Entry point – tema, scaffold e ViewModel
│
├── domain/                                  # 🟢 Camada de Domínio (regras de negócio)
│   ├── model/
│   │   ├── PhoneAuthState.kt                #   Estado da autenticação
│   │   └── PhoneValidationResult.kt         #   Resultado de validação (sealed class)
│   ├── repository/
│   │   └── PhoneHintRepository.kt           #   Interface do repositório (abstração)
│   └── usecase/
│       ├── ValidatePhoneNumberUseCase.kt     #   Validação de número
│       ├── RequestPhoneHintUseCase.kt        #   Solicitar Phone Hint
│       └── ExtractPhoneNumberUseCase.kt      #   Extrair número do resultado
│
├── data/                                    # 🔵 Camada de Dados (frameworks/APIs)
│   └── repository/
│       └── PhoneHintRepositoryImpl.kt       #   Implementação com Google Play Services
│
├── presentation/                            # 🟣 Camada de Apresentação (UI + ViewModel)
│   └── phone/
│       ├── PhoneNumberScreen.kt             #   Composable (View)
│       ├── PhoneNumberViewModel.kt          #   ViewModel (StateFlow + Channel)
│       ├── PhoneNumberUiState.kt            #   Data class do estado da UI
│       ├── PhoneNumberContract.kt           #   Events + Effects (UDF)
│       └── PhoneNumberViewModelFactory.kt   #   Factory manual para DI
│
└── ui/theme/                                # 🎨 Tema Material 3
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

### Fluxo de Dados (UDF – Unidirectional Data Flow)

```
┌──────────┐    Events     ┌───────────┐   Use Cases   ┌──────────┐
│          │ ────────────▶ │           │ ────────────▶ │          │
│   View   │               │ ViewModel │               │  Domain  │
│ (Screen) │ ◀──────────── │           │ ◀──────────── │          │
│          │  UiState +    │           │    Result     │          │
└──────────┘  Effects      └───────────┘               └──────────┘
                                │
                                │ Repository
                                ▼
                          ┌──────────┐
                          │   Data   │
                          │  (Impl)  │
                          └──────────┘
```

1. A **View** (`PhoneNumberScreen`) dispara **Events** para o ViewModel
2. O **ViewModel** processa os events e delega aos **Use Cases**
3. Os Use Cases acessam o **Repository** (interface no domain, implementação no data)
4. O ViewModel atualiza o **UiState** (`StateFlow`) e emite **Effects** (`Channel`) de volta para a View

## 🛠️ Stack Tecnológica

| Componente | Tecnologia | Versão |
|---|---|---|
| **Linguagem** | Kotlin | 2.2.21 |
| **UI** | Jetpack Compose (Material 3) | BOM 2025.11.00 |
| **Gerenciamento de Estado** | StateFlow + Channel | Kotlin Coroutines |
| **ViewModel** | Lifecycle ViewModel Compose | 2.9.4 |
| **Phone Hint** | Google Play Services Auth | 21.3.0 |
| **Build** | Gradle (Kotlin DSL) | 8.13.1 (AGP) |
| **Min SDK** | Android 7.0 | API 24 |
| **Target SDK** | Android 16 | API 36 |

## 📋 Pré-requisitos

- **Android Studio** Meerkat ou superior
- **JDK 17+** (recomendado usar o JBR bundled no Android Studio)
- **Dispositivo/Emulador** com Google Play Services atualizados
- Conta Google com número de telefone cadastrado (para o Phone Hint funcionar)

## 🚀 Como Executar

1. **Clone o repositório**
   ```bash
   git clone https://github.com/darci/Frictionless-Authentication-with-SMS.git
   cd Frictionless-Authentication-with-SMS
   ```

2. **Abra no Android Studio**
   - `File > Open` e selecione a pasta do projeto
   - Aguarde o Gradle sync completar

3. **Execute no dispositivo/emulador**
   - Selecione um device com Google Play Services
   - Clique em **Run ▶** ou `Shift + F10`

4. **Via terminal** (opcional)
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## 📱 Fluxo do Usuário

1. Ao abrir o app, o **Phone Number Hint picker** é exibido automaticamente
2. O usuário seleciona um número de telefone → **navega automaticamente para a tela de OTP**
3. O SMS é recebido → **o código OTP é preenchido e verificado automaticamente**

> **Nota:** O picker do Phone Hint não requer nenhuma permissão. Ele exibe apenas os números de telefone associados ao dispositivo.

## 🧪 Simulando o envio de SMS (Emulador)

Para testar o preenchimento automático do OTP no emulador, envie um SMS no formato do **SMS Retriever API** via `adb`:

```bash
adb emu sms send 5551234567 "<#> Seu código de verificação é: 123456
aO7RN58Cdxu"
```

### Formato obrigatório do SMS

| Parte | Regra |
|---|---|
| `<#>` | Prefixo obrigatório (identifica o SMS para o Retriever) |
| Corpo | Texto livre contendo o código OTP de 6 dígitos |
| Hash | **11 caracteres** na última linha — identifica o app |
| Tamanho total | Máximo **140 bytes** |

### Como descobrir o hash do seu app — `AppSignatureHelper`

O SMS Retriever API exige que o SMS termine com um **hash de 11 caracteres** que identifica unicamente o app. Esse hash é calculado pela classe [`AppSignatureHelper`](app/src/main/java/io/github/darci/smsauthentication/data/util/AppSignatureHelper.kt).

#### Como o hash é gerado

```
SHA-256( packageName + " " + certificadoDeAssinatura )
    → trunca para 9 bytes
    → codifica em Base64 (sem padding)
    → pega os primeiros 11 caracteres
```

| Entrada | Valor |
|---|---|
| Package name | `io.github.darci.smsauthentication` |
| Certificado | Signing key do APK (debug ou release) |
| **Saída** | Ex.: `aO7RN58Cdxu` |

#### Como obter o hash

1. Execute o app no dispositivo/emulador
2. Abra o **Logcat** no Android Studio
3. Filtre por `AppSignatureHelper`
4. O log mostrará:
   ```
   D/AppSignatureHelper: App hashes: [aO7RN58Cdxu]
   ```
5. Use esse hash na última linha do SMS de teste

> ⚠️ O hash muda entre **debug** e **release** porque cada build variant usa uma signing key diferente. Gere o hash correto para cada variante.

#### Uso em produção

Em produção, o `AppSignatureHelper` **não deve ser incluído no APK**. O hash deve ser:
1. Gerado uma única vez com a signing key de release
2. Configurado no **backend** que envia os SMS
3. Removido do código do app (é um utilitário apenas para debug)

## 🔮 Próximos Passos

- [ ] Implementar envio real de SMS (ex.: Firebase Auth, Twilio)
- [ ] Injeção de dependência com **Hilt** (substituir as `ViewModelFactory` manuais)
- [ ] Testes unitários para Use Cases e ViewModel
- [ ] Testes de UI com Compose Testing
- [ ] Modularização em módulos Gradle (`:domain`, `:data`, `:app`)

## 📄 Licença

Este projeto é uma prova de conceito para fins de estudo e demonstração.
