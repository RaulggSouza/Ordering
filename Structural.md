| Class                       | Line | Justification                                                                                                                                       |
|-----------------------------|------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| Address                     | 24 - 42 | Getters não são testados em testes unitários não podendo aumentar seu coverage (branch coverage em menos de 100% nos getters parece um erro da IDE) |
| Order                       | 190 - 196 | Getters não são testados em testes unitários não podendo aumentar seu coverage                                                                      |
| ListOrdersService           | - | Este service não faz parte da especificação tendo sido criado somente para uso interno                                                              |
| User                        | - | Esta classe é usada somente na autentificação, não sendo testada no domínio                                                                         |
| UserRole                    | - | Este enum é usado somente na autentificação, não sendo testado no domínio                                                                           |
| InvalidCredentialsException | - | Esta exception é usada somente na autentificação, não sendo testada no domínio                                                                      |
| JwtValidationException      | - | Esta exception é usada somente na autentificação, não sendo testada no domínio                                                                      |
| UserAlreadyExistsException  | - | Esta exception é usada somente na autentificação, não sendo testada no domínio                                                                      |
| UserId                      | - | Esta classe é usada somente na autentificação, não sendo testada no domínio                                                                         |
