// src/components/ContrasenyaModal.tsx
import React, { useState } from "react";
import {
Modal,
PasswordInput,
Button,
Text,
Stack,
Alert,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { changePassword } from "../api/usuaris"; // 👈 importa la funció d'API

interface Props {
opened: boolean;
onSuccess: () => void;
}

export default function ContrasenyaModal({ opened, onSuccess }: Props) {
const [nova, setNova] = useState("");
const [repetir, setRepetir] = useState("");
const [error, setError] = useState("");

const handleCanvi = async () => {
    if (nova !== repetir) {
    setError("Les contrasenyes no coincideixen");
    return;
    }

    try {
      await changePassword("temporal", nova); // 👈 assumim temporal com oldPassword
    localStorage.setItem("contrasenyaTemporal", "false");

    showNotification({
        title: "Contrasenya actualitzada",
        message: "Has canviat la contrasenya correctament",
        color: "teal",
    });

    onSuccess();
    } catch (err) {
    setError("Error al canviar la contrasenya");
    console.error(err);
    }
};

return (
    <Modal opened={opened} onClose={() => {}} withCloseButton={false} centered>
    <Stack>
        <Text fz="lg" fw={500}>
        Canvia la teva contrasenya temporal
        </Text>
        <PasswordInput
        label="Nova contrasenya"
        value={nova}
        onChange={(e) => setNova(e.currentTarget.value)}
        />
        <PasswordInput
        label="Repeteix contrasenya"
        value={repetir}
        onChange={(e) => setRepetir(e.currentTarget.value)}
        />
        {error && <Alert color="red">{error}</Alert>}
        <Button onClick={handleCanvi}>Guardar nova contrasenya</Button>
    </Stack>
    </Modal>
);
}
