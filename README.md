# Clite-Compiler
Clite 문법 구현



- Token, TokenType
- Lexical Analysis = Lexer = 어휘 분석기 Ascii char-> Token
- Syntatic Analysis = Parser = 구문 분석기 Token -> Abstract Syntax Tree
- StaticTypeCheck = 정적 타입 유효성 체크 / 함수명, 함수명 중복 체크 / main은 하나 / 변수 타입은 void X
- TypeTransformer = 압묵적 확대 변환 / 연산 타입 결정
- Semantics = 의미 해석 / 상태를 관리

