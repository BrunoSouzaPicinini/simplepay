# Desafio Back-end 

Abaixo você encontrará todos as informações necessárias para iniciar o seu teste.

## Avisos antes de começar

- Leia com atenção este documento todo e tente seguir ao **máximo** as instruções;

> Valorizamos uma boa estrutura de containeres criada por você.

## Objetivo: Pagamentos Simplificados

O Projeto é uma plataforma de pagamentos simplificada. Nela é possível depositar e realizar transferências
de dinheiro entre usuários. Temos 2 tipos de usuários, os comuns e lojistas, ambos têm carteira com dinheiro e realizam
transferências entre eles.

### Requisitos

A seguir estão algumas regras de negócio que são importantes para o funcionamento do Projeto:

- Para ambos tipos de usuário, precisamos do `Nome Completo`, `CPF`, `e-mail` e `Senha`. CPF/CNPJ e e-mails devem ser
  únicos no sistema. Sendo assim, seu sistema deve permitir apenas um cadastro com o mesmo CPF ou endereço de e-mail;

- Usuários podem enviar dinheiro (efetuar transferência) para lojistas e entre usuários;

- Lojistas **só recebem** transferências, não enviam dinheiro para ninguém;

- Validar se o usuário tem saldo antes da transferência;

- Antes de finalizar a transferência, deve-se consultar um serviço autorizador externo, use este mock
  [https://util.devi.tools/api/v2/authorize](https://util.devi.tools/api/v2/authorize) para simular o serviço
  utilizando o verbo `GET`;

- A operação de transferência deve ser uma transação (ou seja, revertida em qualquer caso de inconsistência) e o
  dinheiro deve voltar para a carteira do usuário que envia;

- No recebimento de pagamento, o usuário ou lojista precisa receber notificação (envio de email, sms) enviada por um
  serviço de terceiro e eventualmente este serviço pode estar indisponível/instável. Use este mock
  [https://util.devi.tools/api/v1/notify)](https://util.devi.tools/api/v1/notify)) para simular o envio da notificação
  utilizando o verbo `POST`;

- Este serviço deve ser RESTFul.

> Tente ser o mais aderente possível ao que foi pedido, mas não se preocupe se não conseguir atender a todos os
> requisitos. Durante a entrevista vamos conversar sobre o que você conseguiu fazer e o que não conseguiu.

### Endpoint de transferência

Você pode implementar o que achar conveniente, porém vamos nos atentar **somente** ao fluxo de transferência entre dois
usuários. A implementação deve seguir o contrato abaixo.

```http request
POST /transfer
Content-Type: application/json

{
  "value": 100.0,
  "payer": 4,
  "payee": 15
}
```

Caso ache interessante, faça uma **proposta** de endpoint e apresente para os entrevistadores :heart:

# Avaliação

Apresente sua solução utilizando o framework que você desejar, justificando a escolha.
Atente-se a cumprir a maioria dos requisitos, pois você pode cumprir-los parcialmente e durante a avaliação vamos bater
um papo a respeito do que faltou.

## O que será avaliado e valorizamos :heart:

Habilidades básicas de criação de projetos backend:
- Conhecimentos sobre REST
- Uso do Git
- Capacidade analítica
- Apresentação de código limpo e organizado

Conhecimentos intermediários de construção de projetos manuteníveis:
- Aderência a recomendações de implementação como as PSRs
- Aplicação e conhecimentos de SOLID
- Identificação e aplicação de Design Patterns
- Noções de funcionamento e uso de Cache
- Conhecimentos sobre conceitos de containers (Docker, Podman etc)
- Documentação e descrição de funcionalidades e manuseio do projeto
- Implementação e conhecimentos sobre testes de unidade e integração
- Identificar e propor melhorias
- Boas noções de bancos de dados relacionais

Aptidões para criar e manter aplicações de alta qualidade:
- Aplicação de conhecimentos de observabilidade
- Utlização de CI para rodar testes e análises estáticas
- Conhecimentos sobre bancos de dados não-relacionais
- Aplicação de arquiteturas (CQRS, Event-sourcing, Microsserviços, Monolito modular)
- Uso e implementação de mensageria
- Noções de escalabilidade
- Boas habilidades na aplicação do conhecimento do negócio no software
- Implementação margeada por ferramentas de qualidade (análise estática, PHPMD, PHPStan, PHP-CS-Fixer etc)
- Noções de PHP assíncrono


## O que será um Diferencial

- Uso de Docker
- Uma cobertura de testes consistente
- Uso de Design Patterns
- Documentação
- Proposta de melhoria na arquitetura
- Ser consistente e saber argumentar suas escolhas
- Apresentar soluções que domina
- Modelagem de Dados
- Manutenibilidade do Código
- Tratamento de erros
- Cuidado com itens de segurança
- Arquitetura (estruturar o pensamento antes de escrever)
- Carinho em desacoplar componentes (outras camadas, service, repository)

## Materiais úteis

- https://hub.packtpub.com/why-we-need-design-patterns/
- https://refactoring.guru/
- https://www.atlassian.com/continuous-delivery/software-testing/types-of-software-testing
- https://github.com/exakat/php-static-analysis-tools
- https://martinfowler.com/articles/microservices.html
- https://www.devmedia.com.br/rest-tutorial/28912