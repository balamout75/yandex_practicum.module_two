#!/bin/bash
set -e

KCADM=/opt/keycloak/bin/kcadm.sh
SERVER=http://keycloak:8080
REALM=demo-realm

echo "⏳ Waiting for Keycloak..."
until curl -sf $SERVER/health/ready > /dev/null; do
  sleep 5
done

echo "🔐 Login"
$KCADM config credentials \
  --server $SERVER \
  --realm master \
  --user $KEYCLOAK_ADMIN \
  --password $KEYCLOAK_ADMIN_PASSWORD

echo "🌍 Create realm"
$KCADM get realms/$REALM >/dev/null 2>&1 || \
$KCADM create realms -s realm=$REALM -s enabled=true

echo "👮 Create roles"
for ROLE in CLIENT MANAGER ADMIN; do
  $KCADM get roles/$ROLE -r $REALM >/dev/null 2>&1 || \
  $KCADM create roles -r $REALM -s name=$ROLE
done

create_user () {
  USERNAME=$1
  PASSWORD=$2

  if $KCADM get users -r $REALM -q username=$USERNAME | grep -q username; then
    echo "User $USERNAME exists"
    return
  fi

  echo "Creating user $USERNAME"
  $KCADM create users -r $REALM -s username=$USERNAME -s enabled=true
  $KCADM set-password -r $REALM --username $USERNAME --new-password $PASSWORD
}

create_user ivan password
create_user sergey password
create_user petr password

echo "🎭 Assign roles"
$KCADM add-roles -r $REALM --uusername ivan   --rolename CLIENT
$KCADM add-roles -r $REALM --uusername sergey --rolename CLIENT
$KCADM add-roles -r $REALM --uusername petr   --rolename MANAGER

echo "✅ Init finished"
