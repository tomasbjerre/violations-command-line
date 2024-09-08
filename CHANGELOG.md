# violations-command-line changelog

Changelog of violations-command-line.

## 3.0.3 (2024-09-08)

### Bug Fixes

-  **sarif**  originalUriBaseIds lookup ([c063a](https://github.com/tomasbjerre/violations-command-line/commit/c063abf43cc9af4) Tomas Bjerre)  

## 3.0.2 (2024-04-05)

### Bug Fixes

-  reverting from native to jar in Docker image ([ba502](https://github.com/tomasbjerre/violations-command-line/commit/ba5029ff780b0a8) Tomas Bjerre)  [#10](https://github.com/tomasbjerre/violations-command-line/issues/10)  

## 3.0.1 (2024-04-02)

### Bug Fixes

-  avoid repeated message in the Coverity parser ([bbc9c](https://github.com/tomasbjerre/violations-command-line/commit/bbc9c1872298a2e) Tomas Bjerre)  

## 3.0.0 (2024-04-01)

### Breaking changes

-  migrate Jargo to Picocli to enable Graalvm ([459e3](https://github.com/tomasbjerre/violations-command-line/commit/459e3192a882de9) Tomas Bjerre)  

## 2.1.1 (2024-04-01)

### Other changes

**docs**


[3cc41](https://github.com/tomasbjerre/violations-command-line/commit/3cc417edade4feb) Tomas Bjerre *2024-03-31 20:26:03*


## 2.1.0 (2024-03-24)

### Features

-  coverity parser ([8135e](https://github.com/tomasbjerre/violations-command-line/commit/8135e095854316e) Tomas Bjerre)  

## 2.0.0 (2024-01-27)

### Breaking changes

-  JGit 6 and Java 11 ([65cfd](https://github.com/tomasbjerre/violations-command-line/commit/65cfd7a6424f575) Tomas Bjerre)  

## 1.25.3 (2023-09-17)

### Bug Fixes

-  avoiding duplicated fingerprints in CodeClimate report ([9833e](https://github.com/tomasbjerre/violations-command-line/commit/9833efb0bedf4b5) Tomas Bjerre)  

## 1.25.2 (2023-09-17)

### Bug Fixes

-  allow MSBuild in Program Files (x86) and support NU1701 ([fc8f4](https://github.com/tomasbjerre/violations-command-line/commit/fc8f4ec0d5d7179) Tomas Bjerre)  

## 1.25.1 (2023-07-30)

## 1.25.0 (2023-06-11)

### Features

-  docker image ([314db](https://github.com/tomasbjerre/violations-command-line/commit/314dbc70765e53d) Tomas Bjerre)  

## 1.24.2 (2023-06-01)

### Bug Fixes

-  massive speed regression when processing lots of files ([591c3](https://github.com/tomasbjerre/violations-command-line/commit/591c325eac98f55) Tomas Bjerre)  

## 1.24.1 (2023-03-16)

### Bug Fixes

-  rule resolution in sarif ([4c860](https://github.com/tomasbjerre/violations-command-line/commit/4c86001fedec814) Tomas Bjerre)  

## 1.24.0 (2023-01-19)

### Features

-  semgrep parser ([0a12b](https://github.com/tomasbjerre/violations-command-line/commit/0a12b69b104428f) Tomas Bjerre)  

## 1.23.0 (2023-01-16)

### Features

-  ansible-later parser ([ac110](https://github.com/tomasbjerre/violations-command-line/commit/ac1108d78f59c74) Tomas Bjerre)  

## 1.22.9 (2023-01-15)

### Bug Fixes

-  relative paths to root in sarif export ([9bd8f](https://github.com/tomasbjerre/violations-command-line/commit/9bd8f5ce395956e) Tomas Bjerre)  

## 1.22.8 (2023-01-15)

### Bug Fixes

-  relative paths to root in sarif export ([e2737](https://github.com/tomasbjerre/violations-command-line/commit/e2737a7d4e3be55) Tomas Bjerre)  

## 1.22.7 (2023-01-15)

### Bug Fixes

-  documenting github actions ([20bf4](https://github.com/tomasbjerre/violations-command-line/commit/20bf41f2fd0ea42) Tomas Bjerre)  

## 1.22.6 (2023-01-15)

### Bug Fixes

-  relative paths in sarif export ([5474e](https://github.com/tomasbjerre/violations-command-line/commit/5474e5e60dad085) Tomas Bjerre)  

## 1.22.5 (2023-01-15)

### Bug Fixes

-  avoid line 0 in sarif parser, invalid ([eb2e4](https://github.com/tomasbjerre/violations-command-line/commit/eb2e404452cb5d4) Tomas Bjerre)  

## 1.22.4 (2023-01-15)

### Bug Fixes

-  producing valid sarif format ([ba169](https://github.com/tomasbjerre/violations-command-line/commit/ba169793feb18fa) Tomas Bjerre)  [#7](https://github.com/tomasbjerre/violations-command-line/issues/7)  

## 1.22.3 (2022-09-24)

### Bug Fixes

-  relative in CodeClimate ([4f374](https://github.com/tomasbjerre/violations-command-line/commit/4f374a06b024e20) Tomas Bjerre)  

## 1.22.1 (2022-09-24)

### Bug Fixes

-  make paths relative in CodeClimate ([d7704](https://github.com/tomasbjerre/violations-command-line/commit/d7704ef76841273) Tomas Bjerre)  

## 1.22.0 (2022-09-01)

### Features

-  stepping violations-lib ([cc92b](https://github.com/tomasbjerre/violations-command-line/commit/cc92bf5253695b2) Tomas Bjerre)  

## 1.21.2 (2022-03-17)

### Bug Fixes

-  correcting groupId ([1ef63](https://github.com/tomasbjerre/violations-command-line/commit/1ef6321728d94b4) Tomas Bjerre)  

## 1.21.1 (2022-03-16)

### Bug Fixes

-  sarif parser with location references ([a1025](https://github.com/tomasbjerre/violations-command-line/commit/a10253185ae9f98) Tomas Bjerre)  

## 1.21.0 (2021-12-11)

### Features

-  Dart MACHINE parser ([ab994](https://github.com/tomasbjerre/violations-command-line/commit/ab9947c0c710e4b) Tomas Bjerre)  [#142](https://github.com/tomasbjerre/violations-command-line/issues/142)  

## 1.20.5 (2021-12-07)

### Bug Fixes

-  SARIF ([72c05](https://github.com/tomasbjerre/violations-command-line/commit/72c05c08d44d359) Tomas Bjerre)  

## 1.20.0 (2021-12-07)

### Features

-  sarif ([42966](https://github.com/tomasbjerre/violations-command-line/commit/42966e069c22f79) Tomas Bjerre)  
-  using npm-java-runner ([36196](https://github.com/tomasbjerre/violations-command-line/commit/36196e167682267) Tomas Bjerre)  

## 1.18.0 (2021-07-12)

### Features

-  adding configurable Jacoco parser ([10019](https://github.com/tomasbjerre/violations-command-line/commit/10019b527ec5ac8) Tomas Bjerre)  

## 1.17.0 (2021-07-01)

### Features

-  config file ([1f91d](https://github.com/tomasbjerre/violations-command-line/commit/1f91dc8360ac6a1) Tomas Bjerre)  [#2](https://github.com/tomasbjerre/violations-command-line/issues/2)  

## 1.16.0 (2021-06-16)

### Features

-  MSBuild parser ([c7b3f](https://github.com/tomasbjerre/violations-command-line/commit/c7b3f710150f3f3) Tomas Bjerre)  
-  MSBuild parser ([030f1](https://github.com/tomasbjerre/violations-command-line/commit/030f1b84ffb3019) Tomas Bjerre)  

## 1.15.0 (2021-04-05)

### Other changes

**new build script**


[637e7](https://github.com/tomasbjerre/violations-command-line/commit/637e7010efde113) Tomas Bjerre *2021-04-05 11:52:23*


## 1.13 (2020-10-04)

### Other changes

**check_name and engine_name in CodeClimate**


[630da](https://github.com/tomasbjerre/violations-command-line/commit/630da33016ca8a0) Tomas Bjerre *2020-10-04 10:25:40*


## 1.12 (2020-10-04)

### Other changes

**check_name and engine_name in CodeClimate #112**


[fe8fd](https://github.com/tomasbjerre/violations-command-line/commit/fe8fdc33daa2a16) Tomas Bjerre *2020-10-04 07:33:33*


## 1.11 (2020-09-27)

### Other changes

**Find Security Bugs messages**


[2c62b](https://github.com/tomasbjerre/violations-command-line/commit/2c62be0deab14b8) Tomas Bjerre *2020-09-27 16:25:40*

**Find Security Bugs messages**


[e4f7b](https://github.com/tomasbjerre/violations-command-line/commit/e4f7bec65726c97) Tomas Bjerre *2020-09-27 16:25:05*


## 1.10 (2020-09-23)

### Other changes

**Allow Checkstyle reports with no line**


[c4844](https://github.com/tomasbjerre/violations-command-line/commit/c48443b111462b2) Tomas Bjerre *2020-09-23 15:54:04*


## 1.9 (2020-09-20)

### Other changes

**violations-lib 1.128**


[83614](https://github.com/tomasbjerre/violations-command-line/commit/83614cf9591932f) Tomas Bjerre *2020-09-20 13:45:54*


## 1.8 (2020-07-23)

### Other changes

**ignore codeclimate json**


[a8646](https://github.com/tomasbjerre/violations-command-line/commit/a8646f2cdf33a14) Tomas Bjerre *2020-07-23 05:04:05*

**Correcting arity #1**


[7f1f7](https://github.com/tomasbjerre/violations-command-line/commit/7f1f766f2cf6b6c) Tomas Bjerre *2020-07-23 05:02:07*


## 1.7 (2020-07-05)

## 1.6 (2020-02-03)

### Other changes

**CPPCheckParser with auto closed <error/> tags tomasbjerre/violations-lib#82**


[098bf](https://github.com/tomasbjerre/violations-command-line/commit/098bf2345260f16) Tomas Bjerre *2020-02-03 16:52:32*


## 1.5 (2020-01-03)

### Other changes

**Add support for sonar issue report formats >= v7.5 tomasbjerre/violations-lib#80**


[762dc](https://github.com/tomasbjerre/violations-command-line/commit/762dcbdfa148382) Tomas Bjerre *2020-01-03 06:53:49*


## 1.4 (2019-10-25)

### Other changes

**Prettier output**


[962be](https://github.com/tomasbjerre/violations-command-line/commit/962be4a38fd75ca) Tomas Bjerre *2019-10-25 11:02:42*

**doc**


[98b03](https://github.com/tomasbjerre/violations-command-line/commit/98b03b401386512) Tomas Bjerre *2019-10-09 17:02:00*

**Create FUNDING.yml**


[360e8](https://github.com/tomasbjerre/violations-command-line/commit/360e88fbf067390) Tomas Bjerre *2019-09-28 06:59:38*


## 1.3 (2019-09-07)

### Other changes

**CodeClimate and exports**


[1b197](https://github.com/tomasbjerre/violations-command-line/commit/1b197c14276385f) Tomas Bjerre *2019-09-07 13:02:53*


## 1.2 (2019-08-03)

### Other changes

**Changing name of JSHINT parser to JSLINT**


[0fe77](https://github.com/tomasbjerre/violations-command-line/commit/0fe772cfe9fa257) Tomas Bjerre *2019-08-03 12:13:30*

**doc**


[24ff1](https://github.com/tomasbjerre/violations-command-line/commit/24ff17e322091e4) Tomas Bjerre *2019-06-01 10:33:52*

**first**


[49d6c](https://github.com/tomasbjerre/violations-command-line/commit/49d6cee69d71d71) Tomas Bjerre *2019-05-30 21:26:42*


