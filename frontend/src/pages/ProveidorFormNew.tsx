// src/pages/ProveidorFormNew.tsx

import React from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import {
  Paper,
  TextInput,
  Button,
  Flex,
  Group,
  Title,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { IconCheck, IconX } from "@tabler/icons-react";
import { useCreateProveidor } from "../api/proveidors";

type FormValues = {
  nomComercial: string;
  nom: string;
  nif: string;
  adreca: string;
};

export default function ProveidorFormNew() {
  const navigate = useNavigate();
  const createMutation = useCreateProveidor();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: {
      nomComercial: "",
      nom: "",
      nif: "",
      adreca: "",
    },
  });

  const onSubmit: SubmitHandler<FormValues> = (data) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        showNotification({
          title: "Correcte",
          message: "Proveïdor creat correctament",
          icon: <IconCheck size={18} />,
          color: "teal",
        });
        navigate("/proveidors");
      },
      onError: (err: unknown) => {
        const msg = err instanceof Error ? err.message : String(err);
        showNotification({
          title: "Error",
          message: msg,
          icon: <IconX size={18} />,
          color: "red",
        });
      },
    });
  };

  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{ maxWidth: 600, margin: "auto",fontFamily: "Poppins, sans-serif" }}
    >
      <Title order={3} mb="md">
        Nou Proveïdor
      </Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Flex direction="column" gap="md">
          <TextInput
            size="sm"
            label="Nom Comercial"
            placeholder="Nom comercial del proveïdor"
            {...register("nomComercial", {
              required: "El nom comercial és obligatori",
            })}
            error={errors.nomComercial?.message}
          />
          <TextInput
            size="sm"
            label="Nom"
            placeholder="Nom del proveïdor"
            {...register("nom", {
              required: "El nom és obligatori",
            })}
            error={errors.nom?.message}
          />
          <TextInput
            size="sm"
            label="NIF"
            placeholder="NIF del proveïdor"
            {...register("nif", {
              required: "El NIF és obligatori",
              pattern: {
                value: /^[A-Z0-9]{8,20}$/,
                message: "El NIF no és vàlid",
              },
            })}
            error={errors.nif?.message}
          />
          <TextInput
            size="sm"
            label="Adreça"
            placeholder="Adreça del proveïdor"
            {...register("adreca", {
              required: "L'adreça és obligatòria",
            })}
            error={errors.adreca?.message}
          />

          <Group justify="apart" mt="xl">
            <Button
              size="sm"
              type="submit"
              loading={createMutation.status === "pending"}
            >
              Crea
            </Button>
            <Button
              size="sm"
              variant="outline"
              onClick={() => navigate("/proveidors")}
            >
              Cancel·la
            </Button>
          </Group>
        </Flex>
      </form>
    </Paper>
  );
}