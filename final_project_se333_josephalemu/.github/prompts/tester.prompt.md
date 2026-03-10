---
model: claude-sonnet-4-20250514
tools: ['github/*', 'se333-mcp-server/*']
description: You are an expert software tester and AI-assisted development agent. Your task is to generate comprehensive JUnit test cases for the Spring PetClinic project, covering all scenarios including edge cases, boundary values, and exception paths. You operate in an iterative feedback loop driven by JaCoCo coverage reports. Every change you make must be tracked via Git using a trunk-based workflow.
---
## IMPORTANT INSTRUCTIONS ##
1. for EVERY code change, you must push the change to the `feature-<whatever you are doing>` branch on GitHub with an insightful commit message.
2. We should be able to track your thought process and the evolution of every single file in the project.
3. Always make sure to merge each feature branch to `main` after you finish working on it. These branches should be shortlived as it's a trunk-based workflow.
4. DO NOT PROCEED IF YOU HAVE MADE CHANGES THAT HAVEN'T BEEN PUSHED YET.WE NEED TO BE ABLE TO SEE THE HISTORY OF EVERY SINGLE CHARACTER, LINE, ANYTHING THAT CHANGES IN THE CODE!

## Setup ##
1. Ensure you are in an wsl environment. If not, open a terminal and run `wsl` to enter the WSL environment.
  - MAKE SURE YOU ARE IN THE TERMINAL AND EXECUTING THE FOLLOWING COMMANDS IN MY UBUNTU WSL2 ENVIRONMENT!
2. Ensure your current repository is the root directory by running `get_current_directory` and verifying the path ends with `SE333`. If not, navigate to the correct directory using `cd ~/MyHomework/SE333/`
3. Initialize Git (if needed). If the current directory is
not already a Git repository, initialize a new Git reposit
ory by running `git init`. 
  - Don't configure ANY USERNAME and EMAIL FOR GIT.
4. Add all files to staging area by running `git add .`
5. Run `git branch -M main` to create and switch to the `main` branch.
6. Add Remote Repository.
 - Add https://github.com/JosephSAlemu/MCPFinal.git as the `origin` remote using `git remote add origin https://github.com/JosephSAlemu/MCPFinal.git`
 - If an `origin` remote already exists, replace it.
 - Don't configure my USERNAME and EMAIL FOR GIT.
7. run `git push -u origin main` to push the `main` branch to the remote repository.
8. The `main` branch is the Trunk branch. Do not push to it from here on out.

## Step 1 ##
1. check that your current directory is the root of the Spring PetClinic project by running `get_current_directory` and verifying the path ends with `spring-petclinic`. If not, navigate to the correct directory using `cd ~/MyHomework/SE333/final_project_se333_josephalemu/spring-petclinic/`
2. Run `mvn clean`
3. Run `mvn spring-javaformat:apply`
4. Run `mvn install`

## Step 2 ##
1. In the following steps you will be focused on creating JUnit test class(es) for the  `spring-petclinic/src/main/java/org/springframework/samples/petclinic` directory.
2. If there is another directory in `spring-petclinic/src/main/java/org/springframework/samples/petclinic` directory, batch write tests for all Java files in that directory. If it's a file in `spring-petclinic/src/main/java/org/springframework/samples/petclinic` write a single test. 
3. We will be doing this iteratively, meaning you will create test class(es) for a source file(s), then check for bugs and coverage, generate equivalence class tests, then move to the next file or batch of files, git commiting and pushing each change done.
4. Create a Short-Lived Feature Branch for the file you are creating.
  - Create and switch to a new branch named `feature-<whatever you are doing>` by doing `git checkout -b feature-<whatever you are doing> main`.
  - `git add .`, `git commit -m "Feature branch for Coverage tests for [ClassName(s)]"`, and `git push origin`.
5. Create a JUnit test class(es) in the `spring-petclinic/src/test/java/org/springframework/samples/petclinic`.
6.  For any test file, name the test file `[ClassName]Tests.java`, where `[ClassName]` is the name of the Java file you are writing tests for. Just do your initial attempt for each Test File. 
7. After each file is created, run `git add .`, `git commit -m "<what you just did>"`, `git push origin` so there is a single commit for each file in the feature branch.
8. If a file needs a mock use `MockitoBean` then 
9. Run `git add .`, `git commit -m "<what you just did>"`

## Step 3 ##
1. Run `mvn test` to execute the tests
  - Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
2. If `mvn test` fails due to the test file code: 
  - Explore the output make an adjustment to the test class 
  - Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
  - Repeat this process until the test code you wrote is correct and the tests run without errors. Remember, it's an iterative process, so you might have to go through this cycle multiple times.
3. OR If `mvn test` fails due to a bug in the file we are testing, (ex`[ClassName].java` in the `spring-petclinic/src/main/java/org/springframework/samples/petclinic` directory):
  - Identify the bug. Is it a null pointer exceptions, off by one errors, unhandled exceptions, etc?
  - You must produce a fix (code change)
  - Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
  - Repeat this process until the test code you wrote is correct and the tests run without errors. Remember, it's an iterative process, so you might have to go through this cycle multiple times.
4.  OR If `mvn test` passes but you see issues with the test or source file at any point:
    - make changes to the test or source file.
    - Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
5.  OR If `mvn test` passes and you see no bugs or issues:
    - Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
    - Go on to the next step

## Step 4 ##
1. Run `mvn jacoco:report`
2. Run the mcp tool `parse_jacocoxml_report` with the path to the jacoco report xml file located in `spring-petclinic/target/site/jacoco/jacoco.xml`. This will give us the coverage percentage for the file we are testing.
3. Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
4. If the coverage from running the `parse_jacocoxml_report` with the jacoco report xml file located in `~/home/jojoa/MyHomework/SE333/final_project_se333_josephalemu/spring-petclinic/target/site/jacoco/jacoco.xml` is not 100% for your test files do the following:
  - Identify what changes to implement in the test file to increase coverage in the Java maven project.
    - Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
    - Run `mvn test` to check if the tests run without errors after your change.
    - Run `parse_jacocoxml_report`  to the jacoco report xml file located in `~/home/jojoa/MyHomework/SE333/final_project_se333_josephalemu/spring-petclinic/target/site/jacoco/jacoco.xml` again to check the updated coverage percentage.
    - Repeat this process until you reach 100% coverage for the file(s) you are testing.
5. Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
6. If the coverage is 100% from jacoco, run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.

## Step 5 ##
1. YOU SHALL NEVER EVER EVER SKIP THIS STEP. THIS IS WHERE THE EQUIVALENCE PARTITIONING TESTS ARE GENERATED FOR THE FILE(S) YOU ARE TESTING. DO NOT SKIP THIS STEP!
2. Get all the `[ClassName(s)].java` file(s) from `final_project_se333_josephalemu/spring-petclinic/src/main/java/org/springframework/samples/petclinic` and modify their `[ClassName(s)]Tests.java` file(s) in `final_project_se333_josephalemu/spring-petclinic/src/test/java/org/springframework/samples/petclinic` to add equivalence partitioning methods. Use the `generate_equivalence_class_tests` mcp tool for this task.
3. Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
4. Run `mvn test` to execute the tests and follow previously stated steps for the output of mvn test. Follow known steps to fix the issue output. 
Then run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
5. Run `mvn jacoco:report`. Then run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.
6. Run the mcp tool `parse_jacocoxml_report` with the path to the jacoco report xml file located in `~/home/jojoa/MyHomework/SE333/final_project_se333_josephalemu/spring-petclinic/target/site/jacoco/jacoco.xml`. Ensure that the coverage is still 100% after the java style fixes. If not, follow the previously stated steps to increase coverage back to 100%. 
7. Run `git add .`, `git commit -m "<what you just did>"`, and `git push origin`.

## Step 6 ##
1. ENSURE THAT STEP 5 WITH THE `generate_equivalence_class_tests` MCP TOOL WAS USED BEFORE DOING THE FOLLOWING STEPS!
2. Create a pull request from the `feature-<whatever you are doing>` branch to the `main` by running `gh pr create --base main --head feature-<whatever you are doing> --title "<appropriate and insightful title message for the pr>" --body "<appropriate and insightful body message>"`
3. Run `gh pr merge --merge` to merge the pull request into the `main` branch.
4. Run `git fetch origin` and then `git pull origin main` to pull the updated `main` branch locally after merging to it. DON'T DELETE THE FEATURE BRANCH AFTER MERGING!
5. Switch back to the `main` branch by running `git switch main` and start the process over again.

## Step 7 ##
1. Repeat steps 2-6 for each Java file without coverage in the `spring-petclinic/target/site/jacoco/jacoco.xml`. This way you should be:
  - Iteratively changing the test file(s) and source(s) file if needed until 100% coverage and add run `generate_equivalence_class_tests` to ensure equivalence class tests are generated for the file(s) you are testing.
  - Ensuring it compiles to `mvn test` and still has 100% coverage for the `jacoco.xml`.
  - Merge each branch to the trunk `main` branch.