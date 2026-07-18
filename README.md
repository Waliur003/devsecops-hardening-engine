# Kubernetes Project 2: DevSecOps Hardening Engine

## Project Overview

This repository contains the infrastructure templates and automated workflow blueprints for a production-grade DevSecOps Hardening Engine. In standard enterprise Kubernetes deployments, running workloads on default cluster configurations introduces massive security exposures. Default installations feature flat networking configurations where any container can communicate with internal database layers across separate namespaces, and pods frequently run with unnecessary root administrative OS privileges. If an application container is compromised via an external exploit, attackers can easily move laterally across the internal cluster network to siphon sensitive information or compromise underlying node infrastructure.

This project addresses these critical defensive vulnerabilities by constructing an end-to-end secure software supply chain tied directly to a hardened, zero-trust runtime environment. The architecture ensures that security checks are shifted completely left into an automated pipeline gate, preventing vulnerable images or insecure resource manifests from ever reaching deployment stages, while enforcing strict micro-segmentation and runtime isolation at the cluster level.

## System Architecture

The infrastructure implements a layered security envelope designed to protect workloads from development to ongoing operations.

* **Automated Verification Pipeline Tier:** A continuous integration gate hooks directly into resource repositories, intercepting configuration updates and executing mandatory code vulnerability scans prior to deployment.

* **Identity and Access Boundary Layer:** Granular cluster control-plane access restrictions are configured using customized scoping parameters, stripping out default root management service accounts.

* **Network Isolation Firewall Mesh:** Active network communication block rules segment the namespace, isolating running computing components behind strict ingress boundaries.

* **Hardened Runtime Container Layer:** Compute instances run with hardened operating-system level constraints inside the pod configurations, eliminating access paths to the underlying host hardware execution context.

[INSERT DEFENSIVE ARCHITECTURE DIAGRAM HERE]

## Repository Structure and Component Breakdown

```text
devsecops-hardening-engine/
├── app/
│   ├── server.js
│   ├── package.json
│   └── Dockerfile
└── k8s/
    ├── pipeline.groovy
    ├── rbac.yaml
    ├── network-policy.yaml
    └── deployment.yaml
```

## Application Component Tier (`app/`)

### `server.js`

A microservices backend utility configured to handle application transactions. It features standard communication routes along with internal diagnostic verification links that interface straight with the orchestrator health checking system.

### `package.json`

The application manifest mapping strict production package dependency limitations and engine parameters, ensuring reproducible builds across separate pipeline executions.

### `Dockerfile`

A multi-stage runtime build manifest engineered to eliminate software dependencies that are not strictly necessary for execution. It leverages light alpine base layers, strips out development compilation binaries, and sets custom internal user execution targets to eliminate standard root user operations inside the built container image.

## Declarative Hardening and Pipeline Tier (`k8s/`)

### `pipeline.groovy`

Orchestrates the automated DevSecOps build lifecycle, embedding static manifest auditing gates and container analysis blocks to prevent weak resource definitions from reaching execution environments.

### `rbac.yaml`

Establishes explicit Role-Based Access Control configurations, isolating the workload application inside restricted execution domains by mapping granular system access bindings to dedicated identities.

### `network-policy.yaml`

Enforces an enterprise-grade zero-trust defensive network layout, implementing a global default-deny rule and explicitly whitelisting precise inbound layer vectors.

### `deployment.yaml`

Defines the core computing workload architecture, integrating deep operating-system level container containment constraints and security profiles to neutralize lateral system exploit vulnerabilities.

## Core Engineering Focus Areas

### 1. Automated Shift-Left Security Scans and Quality Gates

Relying on manual auditing reviews to intercept vulnerable container configurations introduces severe operational risk to production cluster deployments. This infrastructure establishes an automated shift-left defensive boundary directly within the continuous integration lifecycle.

The pipeline forces all repository updates through static application security testing (SAST) and manifest auditing engines before any resource is modified inside the cluster. The workflow runner leverages open-source scanning instrumentation to parse the application code dependencies for known vulnerabilities, verify that base image file layers are clear of critical exploits, and cross-reference the Kubernetes infrastructure-as-code manifests against enterprise configuration baselines. If a developer attempts to commit an unapproved configuration adjustment—such as opening a dangerous network port or referencing an unverified base image layer—the automated quality gate fails the pipeline build block immediately, logs the policy violation, and cancels the deployment phase to guarantee cluster compliance.

### 2. Least Privilege Access Controls (RBAC)

Leaving default system access rights unconfigured allows cluster components to interact with internal API control layers using excessive administrative capabilities. To mitigate the risk of account compromise, this architecture implements strict Role-Based Access Control definitions.

Workloads are stripped of default administrative permissions and assigned to unique, single-purpose ServiceAccount identities created explicitly for their specific functional roles. The matching authorization profile is defined via granular Role declarations that explicitly enumerate the exact API verbs and resource target endpoints the application is permitted to interact with. These definitions are explicitly linked together using strict RoleBinding declarations. This limits the application's visibility and prevents an attacker from executing reconnaissance commands, modifying sibling cluster setups, or escalating their privileges if the front-facing computing node is compromised.

### 3. Pod Runtime Hardening and OS-Level Constraints

Securing the network perimeter is insufficient if an application container executes with root-level operating system permissions, allowing a container breakout to compromise the underlying host machine. This project enforces absolute runtime containment by integrating deep SecurityContext definitions straight into the pod configuration layer.

Workload manifests strictly enforce non-root user execution flags, meaning the master orchestration controller will refuse to launch the pod if the container process attempts to initialize using root privileges. Furthermore, the infrastructure mounts the container filesystem using read-only access controls. This prevents an attacker from downloading malicious binaries, injecting shell tracking codes, or altering core system configurations if they exploit an application vulnerability. Any temporary caching or tracing processes are redirected into ephemeral, memory-backed disk spaces that disappear instantly when the container is recycled.

### 4. Strict Defensive Network Segmentation

The default networking design of standard Kubernetes clusters operates on a flat topology, allowing every pod to freely initiate connection pathways to any other pod in the cluster. This infrastructure neutralizes this attack vector by implementing zero-trust network micro-segmentation rules via custom NetworkPolicies.

The namespace initializes under a comprehensive default-deny rule that blocks all unverified ingress and egress network traffic across the entire compute stack. From this secure baseline, explicit whitelist policies are constructed to permit only the exact network traffic pathways required for system operations. The backend database layer is wrapped in a dedicated network policy that rejects all traffic unless the inbound call explicitly matches the labels and selectors of the approved application processing tier. This isolation stops attackers from establishing lateral command-and-control communication channels or probing internal systems from an exposed front-end pod.

## Deployment and Verification Plan

The platform initialization follows a strict, dependent sequence to ensure secure resource linking:

1. **Workload Manifest Scan:** Initiate the repository delivery pipeline to run static manifest analysis checks over the deployment resources, validating that all security context configurations match zero-trust design baselines.

2. **Access Security Provisioning:** Deploy the granular RBAC service profiles and matching role definitions into the target namespace, establishing the least-privilege boundary lines before code execution begins.

3. **Network Micro-Segmentation Isolation:** Apply the defensive network policy templates to block all default communications across the environment, initializing the default-deny firewall envelope.

4. **Hardened Pod Workspace Activation:** Launch the compute workload components. The deployment engine evaluates the structural OS runtime constraints, instantiates the non-root execution environments, activates the read-only file partitions, and establishes the validated communication channels to begin processing secure transaction traffic.

## Production Verification Proofs

This section provides verified visual confirmation that the production system successfully meets enterprise infrastructure requirements.

### 1. DevSecOps Automated Pipeline Execution Proof

The delivery log outputs confirm that the continuous integration pipeline executed perfectly across all stages. The workflow automation successfully passed the source repository code through image optimization steps, compiled the production artifact layers, and validated the overall configuration state before initiating cluster modifications.

[INSERT SCREENSHOT PROOF: CI/CD PIPELINE RUN DASHBOARD LOG GENERATION CONFIRMING SUCCESSFUL COMPLETION STATUS]

### 2. Static Vulnerability Gate and Manifest Audit Intercept Proof

This verification shows the security quality gate actively intercepting and blocking a non-compliant manifest update. When an insecure deployment template configuration was intentionally introduced into the repository, the pipeline analysis tools automatically flagged the policy violations and halted the deployment phase.

[INSERT SCREENSHOT PROOF: TRIVY OR MANIFEST SCANNER TERMINAL OUTPUT INTERCEPTING POLICY FAILURES AND BLOCKING CODE DELIVERY]

### 3. Network Policy Micro-Segmentation and Traffic Block Proof

This test demonstrates absolute network isolation across the cluster environments. The system outputs confirm that while approved application pods can communicate across white-listed endpoints, any unauthorized connection attempts originating from unmapped sibling containers are immediately dropped by the cluster network policy.

[INSERT SCREENSHOT PROOF: TERMINAL LOG DEPICTING SUCCESSFUL internal WORKLOAD TRAFFIC UTILITY ALONGSIDE TIMED-OUT UNAUTHORIZED PACKET DROPS]

### 4. Container Runtime Hardening and Security Context Enforcement Proof

This proof captures the cluster actively enforcing operating-system level container containment constraints. Terminal tracking checks confirm that the application process is running under a restricted non-root user identity, and any unauthorized write operations inside the core system filesystem partitions are rejected with explicit permission-denied errors.

[INSERT SCREENSHOT PROOF: KUBECTL EXEC WORKSPACE DIAGNOSTIC RUN INDICATING WHOAMI ID CHECK AND READ-ONLY FILESYSTEM WRITE DENIAL]

## Future Improvements

### 1. Admission Controller Policy Enforcement

Integrate Kubernetes admission control using tools such as Kyverno or Open Policy Agent Gatekeeper to automatically reject insecure manifests before they ever reach the API server. This would enforce non-root containers, read-only filesystems, resource limits, approved image registries, and restricted capability sets at the cluster control-plane level.

### 2. Runtime Threat Detection with Falco

Deploy Falco as a runtime security monitoring layer to detect suspicious container behavior after workloads are already running. This would provide live alerts for abnormal shell execution, unexpected file writes, privilege escalation attempts, sensitive file access, or suspicious network activity inside pods.

### 3. Image Signing and Supply Chain Verification

Add container image signing using Cosign and verify trusted signatures before deployment. This would ensure that only verified, tamper-free container images from approved build pipelines are allowed to run inside the Kubernetes environment.

### 4. GitOps-Based Secure Deployment Automation

Integrate ArgoCD or FluxCD to synchronize hardened Kubernetes manifests directly from a protected Git repository. This would create a fully auditable GitOps deployment model where every production change is tracked, reviewed, and automatically reconciled against the live cluster state.

### 5. Centralized Security Dashboarding

Connect pipeline scan results, Kubernetes audit logs, Falco alerts, and runtime security events into a centralized observability platform such as Grafana, Prometheus, or OpenSearch. This would give security teams a single dashboard to monitor cluster hardening status, policy violations, and runtime threat activity.