node('Server CICD') {

    stage('Checkout') {
        checkout([
                $class           : 'GitSCM',
                branches         : scm.branches,
                extensions       : scm.extensions + [[$class: 'LocalBranch', localBranch: '']],
                userRemoteConfigs: scm.userRemoteConfigs
        ])
    }

    String branch = env.BRANCH_NAME.toString()

    stage('Maven build') {

        //returns a set of git revisions with the name of the committer
        @NonCPS
        def commitList = {
            def changes = ""
            currentBuild.changeSets.each { set ->
                set.each { entry ->
                    changes += "${entry.commitId} - ${entry.msg} \n by ${entry.author.fullName}\n"
                }
            }
            return changes
        }

        def handleFailures = {
            if (branch.startsWith("ST-")) {
                step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: emailextrecipients([[$class: 'RequesterRecipientProvider']]), sendToIndividuals: true])
            } else {
                step('Send notification') {
                    mail(to: 'server@fullgc.com',
                            subject: "Maven build failed for branch: " + env.BRANCH_NAME.toString(),
                            body: "last commits are: " + commitList().toString() + " (<$BUILD_URL/console|Job>)");
                }
                slackSend channel: 'server', color: 'warning', message: "Maven build failed for branch ${branch} \". \nlast Commits are: \n" + commitList().toString() + "\n (<$BUILD_URL/console|Job>)"
            }
        }

        try {
            withMaven(jdk: 'JDK 8 update 66', maven: 'Maven 3.0.5') {
                sh "mvn -Dmaven.test.failure.ignore=true clean install"
            }
            if (currentBuild.result("UNSTABLE")) {
                handleFailures()
            }
        } catch (Exception e) {
            manager.buildFailure()
            handleFailures()
            throw e
        }
    }

    def pom = readFile 'pom.xml'
    def project = new XmlSlurper().parseText(pom)
    String version = project.version.toString()
    String tarName = "volcano-${version}-release-pack.tar.gz"

    stage('Release') {

        @NonCPS
        def newVersion = {
            def doesFileExist = fileExists 'releases.txt'
            if (doesFileExist) {
                echo "file releases.txt exists"
                def file = readFile 'releases.txt'
                String fileContents = file.toString()
                Integer newTarVersion = fileContents.toInteger() + 1
                writeFile file: 'releases.txt', text: newTarVersion.toString()
                echo "new tar version is: " + newTarVersion
            } else {
                echo "file releases.txt does not exist"
                writeFile file: 'releases.txt', text: "1"
            }
            readFile 'releases.txt'
        }

        if (branch.startsWith("release") || branch.startsWith("hotfix")) {
            String tarRCVersion = newVersion()
            String newTarName = "volcano${version}-RC-${tarRCVersion}-release-pack.tar.gz"
            sh "mv ./volcano/target/${tarName} ./viper/target/${newTarName}"
            tarName = newTarName
        }
        step('Commit and push releases file') {
            sh "git remote set-url origin git@bitbucket.org:fullgc/volcano.git"
            sh "git add -A"
            sh "git commit -m 'update volcano version to '${tarRCVersion}"
            sh "git push"
        }
        step('Upload tar to s3 cli') {
            withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
                sh "pip install --user awscli"
                sh "sudo apt-get -y install awscli"
                sh "aws s3 cp ./volcano/target/${tarName} s3://fullgc/tars/"
            }
        }
    }


    if (!branch.startsWith('ST-')) {
        stage('Deploy') {

            def incrementVersion = {
                def environment = {
                    switch (branch) {
                        case 'develop': 'development.json'
                            break
                        case 'master': 'production.json'
                            break
                        default: 'qa.json'
                    }
                }
                def environmentFileContent = readFile environment
                def environmentJson = new groovy.json.JsonSlurper().parseText(environmentFileContent)
                environmentJson.default_attributes.volcano.version = "volcano-${version}"
                String environmentPrettyJsonString = new groovy.json.JsonBuilder(environmentJson).toPrettyString()
                environmentJson = null
                writeFile file: $environment, text: environmentPrettyJsonString

                sh "git commit -am 'Changed volcano version in $environment env to '${version}"
                sh "git push"
            }

            checkout changelog: false, poll: false, scm: [$class: 'GitSCM', browser: [$class: 'BitbucketWeb', repoUrl: 'https://bitbucket.org/fullgc/chef'], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'LocalBranch', localBranch:
                    '**']], submoduleCfg                        : [], userRemoteConfigs: [[url: 'git@bitbucket.org:fullgc/chef']]]
            dir('environments/') {
                incrementVersion()
            }
            slackSend channel: 'server', color: 'good', message: " New volcano version ${version} is being deployed to $environment"
        }
        if (branch == 'develop' || branch == 'master') sh "rm releases.txt"
    }
}